package com.example.desafio3dsm.controller

import android.content.Context
import com.example.desafio3dsm.model.Usuario
import com.example.desafio3dsm.network.RetrofitClient
import com.example.desafio3dsm.utils.SessionManager
import com.example.desafio3dsm.utils.PasswordHasher

class AuthController(private val context: Context){

    private val api = RetrofitClient.instance
    private val session = SessionManager(context)

    suspend fun login(email: String, password: String): Result<Usuario> {
        return try {

            val passwordHash = PasswordHasher.hash(password)

            val usuarios = api.getUsuarios()
            val usuario = usuarios.find {
                it.email == email && it.password == passwordHash
            }
            if (usuario != null) {
                session.guardarSesion(usuario)
                Result.success(usuario)
            } else {
                Result.failure(Exception("Correo o contraseña incorrectos"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun registrar(
        nombre: String,
        email: String,
        password: String,
        rol: String
    ): Result<Usuario> {
        return try {
            // Verificar que el email no exista
            val usuarios = api.getUsuarios()
            val existe = usuarios.any { it.email == email }
            if (existe) {
                return Result.failure(Exception("Este correo ya está registrado"))
            }

            val passwordHash = PasswordHasher.hash(password)
            val nuevoUsuario = Usuario(
                nombre = nombre,
                email = email,
                password = passwordHash,
                rol = rol
            )
            val creado = api.createUsuario(nuevoUsuario)
            Result.success(creado)
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
}