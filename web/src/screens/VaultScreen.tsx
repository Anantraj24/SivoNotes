import React, { useState } from 'react';
import { 
  Lock, 
  Unlock, 
  Key, 
  Eye, 
  EyeOff, 
  Copy, 
  Check, 
  Plus, 
  Trash2, 
  ShieldCheck, 
  FileText, 
  RefreshCw, 
  ExternalLink,
  AlertCircle
} from 'lucide-react';
import { useApp } from '../context/AppContext';
import { generatePassword } from '../services/crypto';

export const VaultScreen: React.FC = () => {
  const { 
    settings, 
    isVaultUnlocked, 
    vaultPasswords, 
    vaultNotes, 
    setupVault, 
    unlockVault, 
    lockVault, 
    addVaultPassword, 
    deleteVaultPassword, 
    addVaultNote, 
    deleteVaultNote 
  } = useApp();

  // Setup state
  const [setupPin, setSetupPin] = useState('');
  const [setupPinConfirm, setSetupPinConfirm] = useState('');
  const [setupError, setSetupError] = useState('');

  // Unlock state
  const [unlockPin, setUnlockPin] = useState('');
  const [unlockError, setUnlockError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  // Vault Sub Tabs
  const [vaultTab, setVaultTab] = useState<'passwords' | 'notes'>('passwords');

  // New Password Entry Modal
  const [isAddPasswordOpen, setIsAddPasswordOpen] = useState(false);
  const [pwTitle, setPwTitle] = useState('');
  const [pwUser, setPwUser] = useState('');
  const [pwPassword, setPwPassword] = useState('');
  const [pwUrl, setPwUrl] = useState('');
  const [pwNotes, setPwNotes] = useState('');

  // Password visibility map & copied map
  const [visiblePasswords, setVisiblePasswords] = useState<{ [id: string]: boolean }>({});
  const [copiedId, setCopiedId] = useState<string | null>(null);

  // New Private Note Modal
  const [isAddNoteOpen, setIsAddNoteOpen] = useState(false);
  const [privateNoteTitle, setPrivateNoteTitle] = useState('');
  const [privateNoteContent, setPrivateNoteContent] = useState('');

  // Keypad Helper
  const handleKeypadPress = (digit: string) => {
    if (unlockPin.length < 6) {
      setUnlockPin(prev => prev + digit);
      setUnlockError('');
    }
  };

  const handleKeypadBackspace = () => {
    setUnlockPin(prev => prev.slice(0, -1));
  };

  // Perform Unlock
  const handleUnlock = async (e?: React.FormEvent) => {
    if (e) e.preventDefault();
    if (!unlockPin) return;

    setIsSubmitting(true);
    setUnlockError('');
    const success = await unlockVault(unlockPin);
    setIsSubmitting(false);

    if (!success) {
      setUnlockError('Incorrect PIN. Please try again.');
      setUnlockPin('');
    }
  };

  // Perform Setup
  const handleSetup = async (e: React.FormEvent) => {
    e.preventDefault();
    if (setupPin.length < 4) {
      setSetupError('PIN must be at least 4 digits');
      return;
    }
    if (setupPin !== setupPinConfirm) {
      setSetupError('PINs do not match');
      return;
    }

    setIsSubmitting(true);
    const success = await setupVault(setupPin);
    setIsSubmitting(false);
    if (!success) {
      setSetupError('Failed to create vault.');
    }
  };

  const copyToClipboard = (text: string, id: string) => {
    navigator.clipboard.writeText(text);
    setCopiedId(id);
    setTimeout(() => setCopiedId(null), 2000);
  };

  const handleGeneratePassword = () => {
    const generated = generatePassword(16, true, true);
    setPwPassword(generated);
  };

  const handleSavePasswordEntry = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!pwTitle || !pwPassword) return;

    await addVaultPassword({
      title: pwTitle,
      username: pwUser,
      password: pwPassword,
      url: pwUrl || undefined,
      notes: pwNotes || undefined,
    });

    setPwTitle('');
    setPwUser('');
    setPwPassword('');
    setPwUrl('');
    setPwNotes('');
    setIsAddPasswordOpen(false);
  };

  const handleSavePrivateNote = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!privateNoteTitle && !privateNoteContent) return;

    await addVaultNote({
      title: privateNoteTitle || 'Encrypted Note',
      content: privateNoteContent,
      tags: ['Private'],
    });

    setPrivateNoteTitle('');
    setPrivateNoteContent('');
    setIsAddNoteOpen(false);
  };

  // 1. Setup Screen (If user hasn't set a master PIN yet)
  if (!settings.hasVaultPin && !isVaultUnlocked) {
    return (
      <div className="max-w-md mx-auto px-4 py-8 space-y-6 animate-fade-in">
        <div className="text-center space-y-2">
          <div className="w-16 h-16 rounded-3xl bg-gradient-to-tr from-sivo-primary to-sivo-primary-light text-white flex items-center justify-center mx-auto shadow-xl shadow-sivo-primary/30">
            <Lock className="w-8 h-8" />
          </div>
          <h2 className="text-2xl font-bold text-sivo-text-primary dark:text-sivo-dark-text-primary font-['Outfit']">
            Secure Your Vault
          </h2>
          <p className="text-xs text-sivo-text-secondary dark:text-sivo-dark-text-secondary">
            Set a master PIN to encrypt your private passwords and confidential notes with AES-GCM zero-trust storage.
          </p>
        </div>

        <form onSubmit={handleSetup} className="p-6 rounded-3xl bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border shadow-sivo-md space-y-4">
          {setupError && (
            <div className="p-3 rounded-2xl bg-sivo-error-container text-sivo-error text-xs flex items-center gap-2">
              <AlertCircle className="w-4 h-4 shrink-0" />
              <span>{setupError}</span>
            </div>
          )}

          <div>
            <label className="block text-xs font-semibold text-sivo-text-secondary mb-1">
              Create Master PIN (4-6 digits)
            </label>
            <input
              type="password"
              inputMode="numeric"
              maxLength={6}
              value={setupPin}
              onChange={(e) => setSetupPin(e.target.value.replace(/\D/g, ''))}
              placeholder="••••"
              className="w-full text-center text-xl tracking-widest px-4 py-3 rounded-2xl bg-sivo-bg dark:bg-sivo-dark-bg border border-sivo-border dark:border-sivo-dark-border outline-none focus:border-sivo-primary"
              required
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-sivo-text-secondary mb-1">
              Confirm Master PIN
            </label>
            <input
              type="password"
              inputMode="numeric"
              maxLength={6}
              value={setupPinConfirm}
              onChange={(e) => setSetupPinConfirm(e.target.value.replace(/\D/g, ''))}
              placeholder="••••"
              className="w-full text-center text-xl tracking-widest px-4 py-3 rounded-2xl bg-sivo-bg dark:bg-sivo-dark-bg border border-sivo-border dark:border-sivo-dark-border outline-none focus:border-sivo-primary"
              required
            />
          </div>

          <div className="p-3.5 rounded-2xl bg-sivo-primary-container/60 dark:bg-sivo-primary/20 text-xs text-sivo-on-primary-container dark:text-sivo-primary-light flex items-start gap-2">
            <ShieldCheck className="w-4 h-4 shrink-0 mt-0.5" />
            <p className="leading-relaxed">
              Your vault is stored locally and protected with PBKDF2 + AES-GCM 256-bit encryption. Keep your PIN safe as it cannot be reset without losing encrypted vault data.
            </p>
          </div>

          <button
            type="submit"
            disabled={isSubmitting}
            className="w-full py-3 rounded-full bg-sivo-primary hover:bg-sivo-primary-variant text-white font-semibold text-xs shadow-md shadow-sivo-primary/30 transition-all"
          >
            Create Encrypted Vault
          </button>
        </form>
      </div>
    );
  }

  // 2. Vault Locked Screen (Master Doc Section 19)
  if (!isVaultUnlocked) {
    return (
      <div className="max-w-sm mx-auto px-4 py-6 space-y-6 animate-fade-in text-center">
        <div className="space-y-2">
          <div className="w-16 h-16 rounded-3xl bg-gradient-to-tr from-sivo-primary to-sivo-primary-light text-white flex items-center justify-center mx-auto shadow-xl shadow-sivo-primary/30">
            <Lock className="w-8 h-8" />
          </div>
          <h2 className="text-2xl font-bold text-sivo-text-primary dark:text-sivo-dark-text-primary font-['Outfit']">
            Vault Locked
          </h2>
          <p className="text-xs text-sivo-text-secondary dark:text-sivo-dark-text-secondary">
            Your private passwords and notes are securely encrypted.
          </p>
        </div>

        {/* PIN Dots Indicator */}
        <div className="flex justify-center items-center gap-3 my-4">
          {[0, 1, 2, 3].map((index) => (
            <div
              key={index}
              className={`w-4 h-4 rounded-full transition-all duration-200 ${
                unlockPin.length > index
                  ? 'bg-sivo-primary scale-110 shadow-sm shadow-sivo-primary/50'
                  : 'bg-sivo-surface-variant dark:bg-sivo-dark-surface-variant border border-sivo-border'
              }`}
            />
          ))}
        </div>

        {unlockError && (
          <p className="text-xs text-sivo-error font-medium">{unlockError}</p>
        )}

        {/* Numeric Keypad */}
        <div className="grid grid-cols-3 gap-3 max-w-xs mx-auto">
          {['1', '2', '3', '4', '5', '6', '7', '8', '9'].map((digit) => (
            <button
              key={digit}
              onClick={() => handleKeypadPress(digit)}
              className="w-16 h-16 rounded-2xl bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border shadow-sivo-sm hover:border-sivo-primary hover:bg-sivo-surface-variant active:scale-95 transition-all text-lg font-bold text-sivo-text-primary dark:text-sivo-dark-text-primary mx-auto flex items-center justify-center font-['Outfit']"
            >
              {digit}
            </button>
          ))}

          <button
            onClick={() => setUnlockPin('')}
            className="w-16 h-16 rounded-2xl text-xs font-semibold text-sivo-text-secondary hover:text-sivo-text-primary flex items-center justify-center mx-auto"
          >
            Clear
          </button>

          <button
            onClick={() => handleKeypadPress('0')}
            className="w-16 h-16 rounded-2xl bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border shadow-sivo-sm hover:border-sivo-primary hover:bg-sivo-surface-variant active:scale-95 transition-all text-lg font-bold text-sivo-text-primary dark:text-sivo-dark-text-primary mx-auto flex items-center justify-center font-['Outfit']"
          >
            0
          </button>

          <button
            onClick={handleKeypadBackspace}
            className="w-16 h-16 rounded-2xl text-xs font-semibold text-sivo-text-secondary hover:text-sivo-error flex items-center justify-center mx-auto"
          >
            ⌫
          </button>
        </div>

        <button
          onClick={handleUnlock}
          disabled={unlockPin.length < 4 || isSubmitting}
          className="w-full max-w-xs mx-auto py-3 rounded-full bg-sivo-primary hover:bg-sivo-primary-variant disabled:opacity-50 text-white font-semibold text-xs shadow-md shadow-sivo-primary/30 transition-all flex items-center justify-center gap-2"
        >
          <Unlock className="w-4 h-4" />
          Unlock Vault
        </button>
      </div>
    );
  }

  // 3. Vault Unlocked Screen
  return (
    <div className="space-y-6 pb-24 max-w-4xl mx-auto px-4 sm:px-6 pt-4 animate-fade-in">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2.5">
          <div className="p-2 rounded-2xl bg-pastel-mint-bg text-pastel-mint border border-pastel-mint/30">
            <Unlock className="w-5 h-5" />
          </div>
          <div>
            <h2 className="text-2xl font-bold tracking-tight text-sivo-text-primary dark:text-sivo-dark-text-primary font-['Outfit']">
              Private Vault
            </h2>
            <p className="text-xs text-pastel-mint font-semibold flex items-center gap-1">
              <ShieldCheck className="w-3.5 h-3.5" />
              Decrypted in memory (AES-GCM)
            </p>
          </div>
        </div>

        <div className="flex items-center gap-2">
          <button
            onClick={() => {
              if (vaultTab === 'passwords') setIsAddPasswordOpen(true);
              else setIsAddNoteOpen(true);
            }}
            className="flex items-center gap-1.5 px-4 py-2 rounded-full bg-sivo-primary hover:bg-sivo-primary-variant text-white text-xs font-semibold shadow-md transition-all"
          >
            <Plus className="w-4 h-4" />
            {vaultTab === 'passwords' ? 'Add Password' : 'Add Note'}
          </button>

          <button
            onClick={lockVault}
            className="flex items-center gap-1.5 px-3.5 py-2 rounded-full bg-sivo-surface-variant dark:bg-sivo-dark-surface-variant hover:bg-sivo-error-container hover:text-sivo-error text-sivo-text-secondary text-xs font-semibold transition-all"
            title="Lock Vault immediately"
          >
            <Lock className="w-3.5 h-3.5" />
            Lock
          </button>
        </div>
      </div>

      {/* Vault Sub Navigation Tabs */}
      <div className="flex items-center gap-1 bg-sivo-surface-variant/80 dark:bg-sivo-dark-surface-variant/80 p-1 rounded-2xl w-fit">
        <button
          onClick={() => setVaultTab('passwords')}
          className={`flex items-center gap-2 px-4 py-2 rounded-xl text-xs font-semibold transition-all ${
            vaultTab === 'passwords'
              ? 'bg-white dark:bg-sivo-dark-surface text-sivo-primary shadow-sivo-sm'
              : 'text-sivo-text-secondary hover:text-sivo-text-primary'
          }`}
        >
          <Key className="w-3.5 h-3.5" />
          Passwords ({vaultPasswords.length})
        </button>

        <button
          onClick={() => setVaultTab('notes')}
          className={`flex items-center gap-2 px-4 py-2 rounded-xl text-xs font-semibold transition-all ${
            vaultTab === 'notes'
              ? 'bg-white dark:bg-sivo-dark-surface text-sivo-primary shadow-sivo-sm'
              : 'text-sivo-text-secondary hover:text-sivo-text-primary'
          }`}
        >
          <FileText className="w-3.5 h-3.5" />
          Private Notes ({vaultNotes.length})
        </button>
      </div>

      {/* PASSWORDS TAB */}
      {vaultTab === 'passwords' && (
        <div className="space-y-3">
          {vaultPasswords.length === 0 ? (
            <div className="p-12 text-center rounded-3xl bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border shadow-sivo-sm my-4">
              <div className="w-12 h-12 rounded-2xl bg-sivo-primary-container text-sivo-primary flex items-center justify-center mx-auto mb-3">
                <Key className="w-6 h-6" />
              </div>
              <h3 className="text-base font-bold text-sivo-text-primary dark:text-sivo-dark-text-primary font-['Outfit']">
                No passwords saved
              </h3>
              <p className="text-xs text-sivo-text-secondary dark:text-sivo-dark-text-secondary mt-1 mb-4">
                Store website credentials, app logins, or encryption keys securely.
              </p>
              <button
                onClick={() => setIsAddPasswordOpen(true)}
                className="px-4 py-2 rounded-full bg-sivo-primary text-white text-xs font-semibold shadow-md"
              >
                Add First Password
              </button>
            </div>
          ) : (
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3.5">
              {vaultPasswords.map((item) => {
                const isVisible = visiblePasswords[item.id] || false;
                const isCopied = copiedId === item.id;

                return (
                  <div
                    key={item.id}
                    className="p-4 rounded-2xl bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border shadow-sivo-sm hover:shadow-sivo-md transition-all flex flex-col justify-between"
                  >
                    <div>
                      <div className="flex items-start justify-between mb-2">
                        <div>
                          <h4 className="text-sm font-bold text-sivo-text-primary dark:text-sivo-dark-text-primary font-['Outfit']">
                            {item.title}
                          </h4>
                          {item.url && (
                            <a
                              href={item.url.startsWith('http') ? item.url : `https://${item.url}`}
                              target="_blank"
                              rel="noreferrer"
                              className="text-[11px] text-sivo-primary hover:underline flex items-center gap-1 mt-0.5"
                            >
                              {item.url} <ExternalLink className="w-2.5 h-2.5" />
                            </a>
                          )}
                        </div>

                        <button
                          onClick={() => deleteVaultPassword(item.id)}
                          className="p-1.5 rounded-lg text-sivo-text-muted hover:text-sivo-error hover:bg-sivo-error-container transition-all"
                          title="Delete"
                        >
                          <Trash2 className="w-3.5 h-3.5" />
                        </button>
                      </div>

                      {/* Username */}
                      {item.username && (
                        <div className="mb-2 text-xs text-sivo-text-secondary dark:text-sivo-dark-text-secondary flex items-center justify-between">
                          <span className="text-[10px] uppercase font-bold text-sivo-text-muted">Username</span>
                          <span className="font-mono bg-sivo-surface-variant px-2 py-0.5 rounded-md text-[11px]">
                            {item.username}
                          </span>
                        </div>
                      )}

                      {/* Password Field with Mask & Copy */}
                      <div className="p-2.5 rounded-xl bg-sivo-bg dark:bg-sivo-dark-bg border border-sivo-border dark:border-sivo-dark-border flex items-center justify-between gap-2">
                        <span className="font-mono text-xs tracking-wider text-sivo-text-primary dark:text-sivo-dark-text-primary truncate">
                          {isVisible ? item.password : '••••••••••••••••'}
                        </span>

                        <div className="flex items-center gap-1 shrink-0">
                          <button
                            onClick={() => setVisiblePasswords(prev => ({ ...prev, [item.id]: !isVisible }))}
                            className="p-1 rounded-lg hover:bg-sivo-surface-variant text-sivo-text-secondary"
                            title={isVisible ? 'Hide' : 'Reveal'}
                          >
                            {isVisible ? <EyeOff className="w-3.5 h-3.5" /> : <Eye className="w-3.5 h-3.5" />}
                          </button>

                          <button
                            onClick={() => copyToClipboard(item.password, item.id)}
                            className="p-1 rounded-lg hover:bg-sivo-primary-container hover:text-sivo-primary text-sivo-text-secondary transition-all"
                            title="Copy Password"
                          >
                            {isCopied ? <Check className="w-3.5 h-3.5 text-pastel-mint" /> : <Copy className="w-3.5 h-3.5" />}
                          </button>
                        </div>
                      </div>

                      {item.notes && (
                        <p className="text-[11px] text-sivo-text-secondary dark:text-sivo-dark-text-secondary mt-2.5">
                          {item.notes}
                        </p>
                      )}
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>
      )}

      {/* PRIVATE NOTES TAB */}
      {vaultTab === 'notes' && (
        <div className="space-y-3">
          {vaultNotes.length === 0 ? (
            <div className="p-12 text-center rounded-3xl bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border shadow-sivo-sm my-4">
              <div className="w-12 h-12 rounded-2xl bg-sivo-primary-container text-sivo-primary flex items-center justify-center mx-auto mb-3">
                <FileText className="w-6 h-6" />
              </div>
              <h3 className="text-base font-bold text-sivo-text-primary dark:text-sivo-dark-text-primary font-['Outfit']">
                No encrypted private notes
              </h3>
              <p className="text-xs text-sivo-text-secondary dark:text-sivo-dark-text-secondary mt-1 mb-4">
                Write sensitive personal notes, recovery phrases, or private plans.
              </p>
              <button
                onClick={() => setIsAddNoteOpen(true)}
                className="px-4 py-2 rounded-full bg-sivo-primary text-white text-xs font-semibold shadow-md"
              >
                Add Encrypted Note
              </button>
            </div>
          ) : (
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3.5">
              {vaultNotes.map((note) => (
                <div
                  key={note.id}
                  className="p-4 rounded-2xl bg-white dark:bg-sivo-dark-surface border border-sivo-border dark:border-sivo-dark-border shadow-sivo-sm flex flex-col justify-between"
                >
                  <div>
                    <div className="flex items-start justify-between mb-2">
                      <h4 className="text-sm font-bold text-sivo-text-primary dark:text-sivo-dark-text-primary font-['Outfit']">
                        {note.title}
                      </h4>
                      <button
                        onClick={() => deleteVaultNote(note.id)}
                        className="p-1.5 rounded-lg text-sivo-text-muted hover:text-sivo-error hover:bg-sivo-error-container transition-all"
                      >
                        <Trash2 className="w-3.5 h-3.5" />
                      </button>
                    </div>

                    <p className="text-xs text-sivo-text-secondary dark:text-sivo-dark-text-secondary whitespace-pre-wrap leading-relaxed">
                      {note.content}
                    </p>
                  </div>

                  <div className="mt-3 pt-2 border-t border-sivo-border/40 text-[10px] text-pastel-mint font-semibold flex items-center gap-1">
                    <ShieldCheck className="w-3 h-3" /> Encrypted with AES-GCM
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* Modal: Add Password Entry */}
      {isAddPasswordOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-3 bg-black/50 backdrop-blur-sm animate-fade-in">
          <div className="w-full max-w-lg bg-white dark:bg-sivo-dark-surface rounded-3xl border border-sivo-border dark:border-sivo-dark-border shadow-2xl p-6 animate-slide-up">
            <h3 className="text-lg font-bold text-sivo-text-primary dark:text-sivo-dark-text-primary font-['Outfit'] pb-3 border-b border-sivo-border/60">
              New Password Entry
            </h3>

            <form onSubmit={handleSavePasswordEntry} className="mt-4 space-y-3.5">
              <div>
                <label className="block text-xs font-semibold text-sivo-text-secondary mb-1">
                  Website / Service *
                </label>
                <input
                  type="text"
                  placeholder="e.g. GitHub, Google, College Portal"
                  value={pwTitle}
                  onChange={(e) => setPwTitle(e.target.value)}
                  className="w-full px-3.5 py-2 rounded-xl bg-sivo-bg dark:bg-sivo-dark-bg border border-sivo-border text-xs outline-none focus:border-sivo-primary"
                  required
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-sivo-text-secondary mb-1">
                  Username / Email
                </label>
                <input
                  type="text"
                  placeholder="e.g. anant@example.com"
                  value={pwUser}
                  onChange={(e) => setPwUser(e.target.value)}
                  className="w-full px-3.5 py-2 rounded-xl bg-sivo-bg dark:bg-sivo-dark-bg border border-sivo-border text-xs outline-none focus:border-sivo-primary"
                />
              </div>

              <div>
                <div className="flex items-center justify-between mb-1">
                  <label className="block text-xs font-semibold text-sivo-text-secondary">
                    Password *
                  </label>
                  <button
                    type="button"
                    onClick={handleGeneratePassword}
                    className="text-[11px] font-semibold text-sivo-primary hover:underline flex items-center gap-1"
                  >
                    <RefreshCw className="w-3 h-3" /> Generate Secure
                  </button>
                </div>
                <input
                  type="text"
                  placeholder="Enter or generate password"
                  value={pwPassword}
                  onChange={(e) => setPwPassword(e.target.value)}
                  className="w-full font-mono px-3.5 py-2 rounded-xl bg-sivo-bg dark:bg-sivo-dark-bg border border-sivo-border text-xs outline-none focus:border-sivo-primary"
                  required
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-sivo-text-secondary mb-1">
                  URL (Optional)
                </label>
                <input
                  type="text"
                  placeholder="https://github.com"
                  value={pwUrl}
                  onChange={(e) => setPwUrl(e.target.value)}
                  className="w-full px-3.5 py-2 rounded-xl bg-sivo-bg dark:bg-sivo-dark-bg border border-sivo-border text-xs outline-none focus:border-sivo-primary"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-sivo-text-secondary mb-1">
                  Notes (Optional)
                </label>
                <textarea
                  placeholder="Additional security questions or notes..."
                  value={pwNotes}
                  onChange={(e) => setPwNotes(e.target.value)}
                  rows={2}
                  className="w-full px-3.5 py-2 rounded-xl bg-sivo-bg dark:bg-sivo-dark-bg border border-sivo-border text-xs outline-none focus:border-sivo-primary resize-none"
                />
              </div>

              <div className="pt-2 flex justify-end gap-2">
                <button
                  type="button"
                  onClick={() => setIsAddPasswordOpen(false)}
                  className="px-4 py-2 rounded-full text-xs font-semibold text-sivo-text-secondary hover:bg-sivo-surface-variant"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2.5 rounded-full bg-sivo-primary hover:bg-sivo-primary-variant text-white text-xs font-semibold shadow-md"
                >
                  Save Encrypted
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal: Add Private Note */}
      {isAddNoteOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-3 bg-black/50 backdrop-blur-sm animate-fade-in">
          <div className="w-full max-w-lg bg-white dark:bg-sivo-dark-surface rounded-3xl border border-sivo-border dark:border-sivo-dark-border shadow-2xl p-6 animate-slide-up">
            <h3 className="text-lg font-bold text-sivo-text-primary dark:text-sivo-dark-text-primary font-['Outfit'] pb-3 border-b border-sivo-border/60">
              New Encrypted Private Note
            </h3>

            <form onSubmit={handleSavePrivateNote} className="mt-4 space-y-3.5">
              <div>
                <label className="block text-xs font-semibold text-sivo-text-secondary mb-1">
                  Title
                </label>
                <input
                  type="text"
                  placeholder="e.g. Bank Recovery Phrase, Personal Memo"
                  value={privateNoteTitle}
                  onChange={(e) => setPrivateNoteTitle(e.target.value)}
                  className="w-full px-3.5 py-2 rounded-xl bg-sivo-bg dark:bg-sivo-dark-bg border border-sivo-border text-xs outline-none focus:border-sivo-primary"
                  autoFocus
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-sivo-text-secondary mb-1">
                  Content *
                </label>
                <textarea
                  placeholder="Confidential content encrypted locally..."
                  value={privateNoteContent}
                  onChange={(e) => setPrivateNoteContent(e.target.value)}
                  rows={5}
                  className="w-full px-3.5 py-2 rounded-xl bg-sivo-bg dark:bg-sivo-dark-bg border border-sivo-border text-xs outline-none focus:border-sivo-primary resize-none"
                  required
                />
              </div>

              <div className="pt-2 flex justify-end gap-2">
                <button
                  type="button"
                  onClick={() => setIsAddNoteOpen(false)}
                  className="px-4 py-2 rounded-full text-xs font-semibold text-sivo-text-secondary hover:bg-sivo-surface-variant"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2.5 rounded-full bg-sivo-primary hover:bg-sivo-primary-variant text-white text-xs font-semibold shadow-md"
                >
                  Encrypt & Save
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
