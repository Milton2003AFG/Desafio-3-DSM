package com.example.desafio3dsm.controller

import com.example.desafio3dsm.model.Recurso
import com.example.desafio3dsm.network.RetrofitClient

class RecursoController {

    private val api = RetrofitClient.instance

    suspend fun obtenerRecursos(): Result<List<Recurso>> {
        return try {
            Result.success(api.getRecursos())
        } catch (e: Exception) {
            Result.failure(Exception("Error al cargar recursos: ${e.message}"))
        }
    }

    suspend fun buscarRecursos(query: String): Result<List<Recurso>> {
        return try {
            val lista = api.getRecursos()
            val filtrada = lista.filter { r ->
                r.titulo.contains(query, ignoreCase = true) ||
                        r.tipo.contains(query, ignoreCase = true) ||
                        r.id.contains(query, ignoreCase = true)
            }
            Result.success(filtrada)
        } catch (e: Exception) {
            Result.failure(Exception("Error al buscar: ${e.message}"))
        }
    }

    suspend fun filtrarPorTipo(tipo: String): Result<List<Recurso>> {
        return try {
            val lista = api.getRecursos()
            val filtrada = if (tipo == "Todos") lista
            else lista.filter { it.tipo.equals(tipo, ignoreCase = true) }
            Result.success(filtrada)
        } catch (e: Exception) {
            Result.failure(Exception("Error al filtrar: ${e.message}"))
        }
    }

    suspend fun crearRecurso(recurso: Recurso): Result<Recurso> {
        return try {
            Result.success(api.createRecurso(recurso))
        } catch (e: Exception) {
            Result.failure(Exception("Error al crear recurso: ${e.message}"))
        }
    }

    suspend fun actualizarRecurso(id: String, recurso: Recurso): Result<Recurso> {
        return try {
            Result.success(api.updateRecurso(id, recurso))
        } catch (e: Exception) {
            Result.failure(Exception("Error al actualizar recurso: ${e.message}"))
        }
    }

    suspend fun eliminarRecurso(id: String): Result<Unit> {
        return try {
            api.deleteRecurso(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Error al eliminar recurso: ${e.message}"))
        }
    }

    suspend fun obtenerRecursoPorId(id: String): Result<Recurso> {
        return try {
            Result.success(api.getRecursoById(id))
        } catch (e: Exception) {
            Result.failure(Exception("Error al obtener recurso: ${e.message}"))
        }
    }
}