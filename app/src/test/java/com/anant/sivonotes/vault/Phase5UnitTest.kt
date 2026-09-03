package com.anant.sivonotes.vault

import com.anant.sivonotes.security.PasswordStrength
import com.anant.sivonotes.security.VaultCryptoEngine
import com.anant.sivonotes.security.VaultManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class Phase5UnitTest {

    @Test
    fun testPinHashingAndVerification() {
        val pin = "1234"
        val salt = VaultCryptoEngine.generateSalt()

        val hash1 = VaultCryptoEngine.hashPin(pin, salt)
        val hash2 = VaultCryptoEngine.hashPin(pin, salt)

        assertEquals(hash1, hash2)

        val wrongHash = VaultCryptoEngine.hashPin("9999", salt)
        assertNotEquals(hash1, wrongHash)
    }

    @Test
    fun testAesEncryptionDecryptionRoundtrip() {
        val softwareKey = VaultCryptoEngine.generateSoftwareKey()
        val plain = "SuperSecretConfidentialText123!"

        val encrypted = VaultCryptoEngine.encrypt(plain, softwareKey)
        assertTrue(encrypted.contains(":"))
        assertNotEquals(plain, encrypted)

        val decrypted = VaultCryptoEngine.decrypt(encrypted, softwareKey)
        assertEquals(plain, decrypted)
    }

    @Test
    fun testPasswordGeneratorLengthAndChars() {
        val pw = VaultManager.generatePassword(
            length = 20,
            includeUpper = true,
            includeLower = true,
            includeDigits = true,
            includeSymbols = true
        )
        assertEquals(20, pw.length)
        assertTrue(pw.any { it.isUpperCase() || it.isLowerCase() || it.isDigit() })
    }

    @Test
    fun testPasswordStrengthEvaluation() {
        assertEquals(PasswordStrength.WEAK, VaultManager.evaluatePasswordStrength("123"))
        assertEquals(PasswordStrength.FAIR, VaultManager.evaluatePasswordStrength("MyPass123"))
        assertEquals(PasswordStrength.VERY_STRONG, VaultManager.evaluatePasswordStrength("Tr0ub4dor&34!Secure"))
    }
}
