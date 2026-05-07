package com.example.desafio3dsm.utils

object PasswordValidator {
    fun validar(password: String): String? {
        if (password.length < 12) {
            return "Minimio 12 caracteres"
        }
        if (!password.any { it.isUpperCase() }) {
            return "Al menos una mayúscula"
        }
        if (!password.any { it.isLowerCase() }) {
            return "Al menos una minúscula"
        }
        if (!password.any { it.isDigit() }) {
            return "Al menos un número"
        }
        if (!password.any { it in "!@#\$%^&*" })
            return "Debe tener al menos un carácter especial (!@#\$%^&*)"
        return null
    }
}