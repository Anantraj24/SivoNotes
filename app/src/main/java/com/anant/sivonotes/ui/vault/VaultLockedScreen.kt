package com.anant.sivonotes.ui.vault

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.anant.sivonotes.security.BiometricAuthManager
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun VaultLockedScreen(
    viewModel: VaultViewModel,
    onUnlocked: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var enteredPin by remember { mutableStateOf("") }
    var setupPinStep by remember { mutableStateOf(1) } // 1: Enter, 2: Confirm
    var firstPinDraft by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val shakeOffset = remember { Animatable(0f) }

    fun triggerShake() {
        coroutineScope.launch {
            shakeOffset.snapTo(0f)
            shakeOffset.animateTo(20f, tween(50))
            shakeOffset.animateTo(-20f, tween(50))
            shakeOffset.animateTo(15f, tween(50))
            shakeOffset.animateTo(-15f, tween(50))
            shakeOffset.animateTo(0f, tween(50))
        }
    }

    // Auto-prompt Biometrics on open if vault is already setup
    LaunchedEffect(uiState.isVaultSetup) {
        if (uiState.isVaultSetup && uiState.isBiometricEnabled) {
            val activity = context as? FragmentActivity
            if (activity != null && BiometricAuthManager.isBiometricAvailable(context)) {
                BiometricAuthManager.promptBiometric(
                    activity = activity,
                    onSuccess = {
                        viewModel.unlockWithBiometrics()
                        onUnlocked()
                    },
                    onError = { /* Stay on PIN screen */ }
                )
            }
        }
    }

    fun onDigitPress(digit: String) {
        if (enteredPin.length < 4) {
            val newPin = enteredPin + digit
            enteredPin = newPin
            errorMessage = ""

            if (newPin.length == 4) {
                if (!uiState.isVaultSetup) {
                    // SETUP FLOW
                    if (setupPinStep == 1) {
                        firstPinDraft = newPin
                        enteredPin = ""
                        setupPinStep = 2
                    } else {
                        if (newPin == firstPinDraft) {
                            viewModel.setupVault(newPin)
                            onUnlocked()
                        } else {
                            errorMessage = "PINs do not match. Try again."
                            enteredPin = ""
                            setupPinStep = 1
                            triggerShake()
                        }
                    }
                } else {
                    // UNLOCK FLOW
                    if (viewModel.verifyPin(newPin)) {
                        onUnlocked()
                    } else {
                        errorMessage = "Incorrect PIN"
                        enteredPin = ""
                        triggerShake()
                    }
                }
            }
        }
    }

    fun onBackspacePress() {
        if (enteredPin.isNotEmpty()) {
            enteredPin = enteredPin.dropLast(1)
            errorMessage = ""
        }
    }

    fun onBiometricPress() {
        val activity = context as? FragmentActivity
        if (activity != null) {
            BiometricAuthManager.promptBiometric(
                activity = activity,
                onSuccess = {
                    viewModel.unlockWithBiometrics()
                    onUnlocked()
                },
                onError = { err -> errorMessage = err }
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1E272E))
            .statusBarsPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Back Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.weight(0.5f))

        // Shield Icon & Titles
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(Color(0xFF2D3436), CircleShape)
                .border(2.dp, Color(0xFF6C5CE7), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Shield,
                contentDescription = null,
                tint = Color(0xFF6C5CE7),
                modifier = Modifier.size(38.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (!uiState.isVaultSetup) {
                if (setupPinStep == 1) "Create Master PIN" else "Confirm Master PIN"
            } else {
                "Sivo Secure Vault"
            },
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = if (!uiState.isVaultSetup) "Set a 4-digit PIN to encrypt your offline vault"
            else "Enter your master PIN or use biometric scan",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFFB2BEC3)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // PIN Dot Indicators (4 circles)
        Row(
            modifier = Modifier
                .offset { IntOffset(shakeOffset.value.roundToInt(), 0) },
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            for (i in 0 until 4) {
                val isFilled = i < enteredPin.length
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(
                            color = if (isFilled) Color(0xFF6C5CE7) else Color.Transparent,
                            shape = CircleShape
                        )
                        .border(2.dp, if (isFilled) Color(0xFF6C5CE7) else Color(0xFF636E72), CircleShape)
                )
            }
        }

        // Error message
        AnimatedVisibility(visible = errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFFF7675),
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Numeric Keypad Grid
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val rows = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("BIO", "0", "DEL")
            )

            rows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    row.forEach { key ->
                        when (key) {
                            "BIO" -> {
                                if (uiState.isVaultSetup && uiState.isBiometricEnabled) {
                                    KeypadCircle(onClick = { onBiometricPress() }) {
                                        Icon(
                                            imageVector = Icons.Outlined.Fingerprint,
                                            contentDescription = "Biometrics",
                                            tint = Color(0xFF6C5CE7),
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                } else {
                                    Spacer(modifier = Modifier.size(68.dp))
                                }
                            }
                            "DEL" -> {
                                KeypadCircle(onClick = { onBackspacePress() }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Outlined.Backspace,
                                        contentDescription = "Delete",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            else -> {
                                KeypadCircle(onClick = { onDigitPress(key) }) {
                                    Text(
                                        text = key,
                                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 26.sp),
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KeypadCircle(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(68.dp)
            .clip(CircleShape)
            .background(Color(0xFF2D3436))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
