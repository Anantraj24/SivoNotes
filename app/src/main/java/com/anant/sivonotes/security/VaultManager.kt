package com.anant.sivonotes.security

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.SecureRandom

enum class PasswordStrength(val label: String, val score: Float) {
    WEAK("Weak", 0.25f),
    FAIR("Fair", 0.50f),
    STRONG("Strong", 0.75f),
    VERY_STRONG("Unbreakable", 1.0f)
}

class VaultManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("sivo_vault_secure_prefs", Context.MODE_PRIVATE)

    private val _isUnlocked = MutableStateFlow(false)
    val isUnlocked: StateFlow<Boolean> = _isUnlocked.asStateFlow()

    fun isVaultSetup(): Boolean {
        return prefs.contains(KEY_PIN_HASH) && prefs.contains(KEY_SALT)
    }

    fun isBiometricEnabled(): Boolean {
        return prefs.getBoolean(KEY_BIOMETRIC_ENABLED, true)
    }

    fun setBiometricEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
    }

    fun setupVault(pin: String) {
        val salt = VaultCryptoEngine.generateSalt()
        val hash = VaultCryptoEngine.hashPin(pin, salt)
        prefs.edit()
            .putString(KEY_SALT, salt)
            .putString(KEY_PIN_HASH, hash)
            .apply()
        _isUnlocked.value = true
    }

    fun verifyPin(pin: String): Boolean {
        val salt = prefs.getString(KEY_SALT, null) ?: return false
        val savedHash = prefs.getString(KEY_PIN_HASH, null) ?: return false
        val computedHash = VaultCryptoEngine.hashPin(pin, salt)
        val matches = savedHash == computedHash
        if (matches) {
            _isUnlocked.value = true
        }
        return matches
    }

    fun unlockVault() {
        _isUnlocked.value = true
    }

    fun lockVault() {
        _isUnlocked.value = false
    }

    fun resetVault() {
        prefs.edit().clear().apply()
        _isUnlocked.value = false
    }

    companion object {
        private const val KEY_PIN_HASH = "vault_master_pin_hash"
        private const val KEY_SALT = "vault_master_salt"
        private const val KEY_BIOMETRIC_ENABLED = "vault_biometric_enabled"

        fun generatePassword(
            length: Int = 16,
            includeUpper: Boolean = true,
            includeLower: Boolean = true,
            includeDigits: Boolean = true,
            includeSymbols: Boolean = true
        ): String {
            val upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            val lower = "abcdefghijklmnopqrstuvwxyz"
            val digits = "0123456789"
            val symbols = "!@#$%^&*()_+-=[]{}|;:,.<>?"

            val charPool = StringBuilder()
            if (includeUpper) charPool.append(upper)
            if (includeLower) charPool.append(lower)
            if (includeDigits) charPool.append(digits)
            if (includeSymbols) charPool.append(symbols)

            if (charPool.isEmpty()) charPool.append(lower).append(digits)

            val random = SecureRandom()
            val result = StringBuilder(length)
            for (i in 0 until length) {
                val index = random.nextInt(charPool.length)
                result.append(charPool[index])
            }
            return result.toString()
        }

        fun evaluatePasswordStrength(password: String): PasswordStrength {
            if (password.length < 6) return PasswordStrength.WEAK
            var score = 0
            if (password.length >= 8) score++
            if (password.length >= 12) score++
            if (password.any { it.isUpperCase() } && password.any { it.isLowerCase() }) score++
            if (password.any { it.isDigit() }) score++
            if (password.any { !it.isLetterOrDigit() }) score++

            return when {
                score <= 2 -> PasswordStrength.WEAK
                score == 3 -> PasswordStrength.FAIR
                score == 4 -> PasswordStrength.STRONG
                else -> PasswordStrength.VERY_STRONG
            }
        }
    }
}
