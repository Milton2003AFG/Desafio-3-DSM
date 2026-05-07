package com.example.desafio3dsm.utils

import android.content.Context
import com.example.desafio3dsm.model.Usuario

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("Desafio3DSM", Context.MODE_PRIVATE)

    fun guardarSesion(usuario: Usuario) {
        prefs.edit()
            .putString("id", usuario.id)
            .putString("nombre", usuario.nombre)
            .putString("email", usuario.email)
            .putString("rol", usuario.rol)
            .putBoolean("logueado", true)
            .apply()
    }

    fun getRol(): String = prefs.getString("rol", "estudiante") ?: "estudiante"
    fun getId(): String = prefs.getString("id", "") ?: ""
    fun getNombre(): String = prefs.getString("nombre", "") ?: ""
    fun isLogueado(): Boolean = prefs.getBoolean("logueado", false)

    fun cerrarSesion() = prefs.edit().clear().apply()
}