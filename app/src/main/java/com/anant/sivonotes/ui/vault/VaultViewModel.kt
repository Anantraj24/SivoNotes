package com.anant.sivonotes.ui.vault

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.anant.sivonotes.data.local.entity.PrivateNoteEntity
import com.anant.sivonotes.data.local.entity.VaultEntryEntity
import com.anant.sivonotes.data.repository.VaultRepository
import com.anant.sivonotes.security.PasswordStrength
import com.anant.sivonotes.security.VaultManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class VaultUiState(
    val isVaultSetup: Boolean = false,
    val isUnlocked: Boolean = false,
    val entries: List<VaultEntryEntity> = emptyList(),
    val privateNotes: List<PrivateNoteEntity> = emptyList(),
    val weakPasswordsCount: Int = 0,
    val totalEntriesCount: Int = 0,
    val isBiometricEnabled: Boolean = true,
    val isLoading: Boolean = false
)

class VaultViewModel(
    private val vaultRepository: VaultRepository,
    private val vaultManager: VaultManager
) : ViewModel() {

    private val _isSetup = MutableStateFlow(vaultManager.isVaultSetup())

    val uiState: StateFlow<VaultUiState> = combine(
        _isSetup,
        vaultManager.isUnlocked,
        vaultRepository.getAllVaultEntries(),
        vaultRepository.getAllPrivateNotes()
    ) { isSetup: Boolean, isUnlocked: Boolean, entries: List<VaultEntryEntity>, privateNotes: List<PrivateNoteEntity> ->
        val weakCount = entries.count {
            VaultManager.evaluatePasswordStrength(it.encryptedPassword) == PasswordStrength.WEAK
        }
        VaultUiState(
            isVaultSetup = isSetup,
            isUnlocked = isUnlocked,
            entries = entries,
            privateNotes = privateNotes,
            weakPasswordsCount = weakCount,
            totalEntriesCount = entries.size,
            isBiometricEnabled = vaultManager.isBiometricEnabled(),
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = VaultUiState(
            isVaultSetup = vaultManager.isVaultSetup(),
            isUnlocked = vaultManager.isUnlocked.value,
            isLoading = true
        )
    )

    fun setupVault(pin: String) {
        vaultManager.setupVault(pin)
        _isSetup.value = true
    }

    fun verifyPin(pin: String): Boolean {
        return vaultManager.verifyPin(pin)
    }

    fun unlockWithBiometrics() {
        vaultManager.unlockVault()
    }

    fun lockVault() {
        vaultManager.lockVault()
    }

    fun savePasswordEntry(
        id: Long = 0,
        title: String,
        username: String,
        plainPassword: String,
        websiteUrl: String,
        notes: String
    ) {
        viewModelScope.launch {
            if (id > 0) {
                val existing = vaultRepository.getVaultEntryById(id)
                if (existing != null) {
                    vaultRepository.updateVaultEntry(
                        existing.copy(
                            title = title.trim(),
                            username = username.trim(),
                            encryptedPassword = plainPassword,
                            websiteUrl = websiteUrl.trim(),
                            notes = notes.trim(),
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                }
            } else {
                vaultRepository.insertVaultEntry(
                    VaultEntryEntity(
                        title = title.trim(),
                        username = username.trim(),
                        encryptedPassword = plainPassword,
                        websiteUrl = websiteUrl.trim(),
                        notes = notes.trim(),
                        iv = ""
                    )
                )
            }
        }
    }

    fun deletePasswordEntry(entry: VaultEntryEntity) {
        viewModelScope.launch {
            vaultRepository.deleteVaultEntry(entry)
        }
    }

    fun savePrivateNote(
        id: Long = 0,
        title: String,
        plainContent: String
    ) {
        viewModelScope.launch {
            if (id > 0) {
                val existing = vaultRepository.getPrivateNoteById(id)
                if (existing != null) {
                    vaultRepository.updatePrivateNote(
                        existing.copy(
                            title = title.trim(),
                            encryptedContent = plainContent,
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                }
            } else {
                vaultRepository.insertPrivateNote(
                    PrivateNoteEntity(
                        title = title.trim(),
                        encryptedContent = plainContent,
                        iv = ""
                    )
                )
            }
        }
    }

    fun deletePrivateNote(note: PrivateNoteEntity) {
        viewModelScope.launch {
            vaultRepository.deletePrivateNote(note)
        }
    }

    companion object {
        fun provideFactory(
            vaultRepository: VaultRepository,
            vaultManager: VaultManager
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return VaultViewModel(vaultRepository, vaultManager) as T
            }
        }
    }
}
