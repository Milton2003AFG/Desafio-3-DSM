package com.example.desafio3dsm.controller

import android.content.Context
import com.example.desafio3dsm.model.Usuario
import com.example.desafio3dsm.network.RetrofitClient
import com.example.desafio3dsm.utils.SessionManager

class FavoritoController(context: Context) {

    private val api = RetrofitClient.instance
    private val session = SessionManager(context)

    suspend fun toggleFavorito(recursoId: String): Result<Set<String>> {
        return try {
            val usuarioId = session.getId()
            val usuarios = api.getUsuarios()
            val usuario = usuarios.find { it.id == usuarioId }
                ?: return Result.failure(Exception("Usuario no encontrado"))

            val favoritosActuales = usuario.favoritos.toMutableList()
            if (favoritosActuales.contains(recursoId)) {
                favoritosActuales.remove(recursoId)
            } else {
                favoritosActuales.add(recursoId)
            }

            val usuarioActualizado = usuario.copy(favoritos = favoritosActuales)
            api.updateUsuario(usuarioId, usuarioActualizado)

            Result.success(favoritosActuales.toSet())
        } catch (e: Exception) {
            Result.failure(Exception("Error al actualizar favoritos: ${e.message}"))
        }
    }

    suspend fun obtenerFavoritos(): Result<Set<String>> {
        return try {
            val usuarioId = session.getId()
            val usuarios = api.getUsuarios()
            val usuario = usuarios.find { it.id == usuarioId }
                ?: return Result.failure(Exception("Usuario no encontrado"))
            Result.success(usuario.favoritos.toSet())
        } catch (e: Exception) {
            Result.failure(Exception("Error al obtener favoritos: ${e.message}"))
        }
    }

    suspend fun calificarRecurso(recursoId: String, nuevaCalif: Float): Result<Unit> {
        return try {
            val recurso = api.getRecursoById(recursoId)
            val totalActual = recurso.totalRating
            val ratingActual = recurso.rating

            // Calcular nuevo promedio
            val nuevoTotal = totalActual + 1
            val nuevoRating = ((ratingActual * totalActual) + nuevaCalif) / nuevoTotal

            val recursoActualizado = recurso.copy(
                rating = nuevoRating,
                totalRating = nuevoTotal
            )
            api.updateRecurso(recursoId, recursoActualizado)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Error al calificar: ${e.message}"))
        }
    }
}