package com.example.coffesaf

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

class PhoneVerificationManager {
    data class VerificationState(
        val isDialogVisible: Boolean = false,
        val phoneNumber: String = "+7",
        val verificationCode: String = "",
        val isCodeSent: Boolean = false,
        val errorMessage: String? = null
    )

    private var _verificationState by mutableStateOf(VerificationState())
    val verificationState: VerificationState get() = _verificationState

    fun showVerificationDialog() {
        _verificationState = VerificationState(isDialogVisible = true)
    }

    fun hideVerificationDialog() {
        _verificationState = VerificationState()
    }

    fun updatePhoneNumber(number: String) {
        _verificationState = _verificationState.copy(phoneNumber = number)
    }

    fun sendVerificationCode() {
        if (_verificationState.phoneNumber.length >= 11) {
            _verificationState = _verificationState.copy(
                isCodeSent = true,
                errorMessage = null
            )
        } else {
            _verificationState = _verificationState.copy(
                errorMessage = "Введите корректный номер телефона"
            )
        }
    }


    fun updateVerificationCode(code: String) {
        _verificationState = _verificationState.copy(verificationCode = code)
    }

    fun verifyCode(onSuccess: (String) -> Unit) {
        if (_verificationState.verificationCode.length == 4) {
            val phone = _verificationState.phoneNumber
            hideVerificationDialog()
            onSuccess(phone)
        } else {
            _verificationState = _verificationState.copy(
                errorMessage = "Неверный код подтверждения"
            )
        }
    }


    @Composable
    fun VerificationDialog(onSuccess: (String) -> Unit) {
        if (verificationState.isDialogVisible) {
            AlertDialog(
                onDismissRequest = { hideVerificationDialog() },
                title = {
                    Text(
                        "Подтверждение номера",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Чтобы оформить заказ, необходимо ввести свой номер телефона",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        if (!verificationState.isCodeSent) {
                            OutlinedTextField(
                                value = verificationState.phoneNumber,
                                onValueChange = { updatePhoneNumber(it) },
                                label = { Text("Номер телефона") },
                                placeholder = { Text("+7XXXXXXXXXX") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                            )
                        } else {
                            OutlinedTextField(
                                value = verificationState.verificationCode,
                                onValueChange = { updateVerificationCode(it) },
                                label = { Text("Код из SMS") },
                                placeholder = { Text("1234") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }

                        verificationState.errorMessage?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (!verificationState.isCodeSent) {
                                sendVerificationCode()
                            } else {
                                verifyCode(onSuccess)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF003325)
                        )
                    ) {
                        Text(if (!verificationState.isCodeSent) "Отправить код" else "Подтвердить")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { hideVerificationDialog() }
                    ) {
                        Text("Отмена")
                    }
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
