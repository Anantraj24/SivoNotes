// Web Crypto API AES-GCM & PBKDF2 encryption helpers for SivoNotes Vault

const SALT = new Uint8Array([115, 105, 118, 111, 95, 118, 97, 117, 108, 116, 95, 115, 97, 108, 116]); // "sivo_vault_salt"

async function deriveKey(pin: string): Promise<CryptoKey> {
  const enc = new TextEncoder();
  const keyMaterial = await window.crypto.subtle.importKey(
    'raw',
    enc.encode(pin),
    { name: 'PBKDF2' },
    false,
    ['deriveKey']
  );

  return window.crypto.subtle.deriveKey(
    {
      name: 'PBKDF2',
      salt: SALT,
      iterations: 100000,
      hash: 'SHA-256',
    },
    keyMaterial,
    { name: 'AES-GCM', length: 256 },
    false,
    ['encrypt', 'decrypt']
  );
}

export async function hashPin(pin: string): Promise<string> {
  const enc = new TextEncoder();
  const data = enc.encode(pin + '_sivo_pin_salt');
  const hashBuffer = await window.crypto.subtle.digest('SHA-256', data);
  const hashArray = Array.from(new Uint8Array(hashBuffer));
  return hashArray.map(b => b.toString(16).padStart(2, '0')).join('');
}

export async function encryptData(plainText: string, pin: string): Promise<string> {
  const key = await deriveKey(pin);
  const iv = window.crypto.getRandomValues(new Uint8Array(12));
  const enc = new TextEncoder();
  
  const encrypted = await window.crypto.subtle.encrypt(
    {
      name: 'AES-GCM',
      iv: iv,
    },
    key,
    enc.encode(plainText)
  );

  const combined = new Uint8Array(iv.length + encrypted.byteLength);
  combined.set(iv, 0);
  combined.set(new Uint8Array(encrypted), iv.length);

  return btoa(String.fromCharCode(...combined));
}

export async function decryptData(cipherTextBase64: string, pin: string): Promise<string> {
  try {
    const key = await deriveKey(pin);
    const binary = atob(cipherTextBase64);
    const bytes = new Uint8Array(binary.length);
    for (let i = 0; i < binary.length; i++) {
      bytes[i] = binary.charCodeAt(i);
    }

    const iv = bytes.slice(0, 12);
    const data = bytes.slice(12);

    const decrypted = await window.crypto.subtle.decrypt(
      {
        name: 'AES-GCM',
        iv: iv,
      },
      key,
      data
    );

    const dec = new TextDecoder();
    return dec.decode(decrypted);
  } catch {
    throw new Error('Failed to decrypt data. Invalid PIN or corrupted payload.');
  }
}

// Generate random secure password helper
export function generatePassword(length = 16, includeSymbols = true, includeNumbers = true): string {
  const letters = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
  const numbers = '0123456789';
  const symbols = '!@#$%^&*()_+-=[]{}|;:,.<>?';

  let chars = letters;
  if (includeNumbers) chars += numbers;
  if (includeSymbols) chars += symbols;

  let result = '';
  const randomValues = new Uint32Array(length);
  window.crypto.getRandomValues(randomValues);

  for (let i = 0; i < length; i++) {
    result += chars[randomValues[i] % chars.length];
  }
  return result;
}
