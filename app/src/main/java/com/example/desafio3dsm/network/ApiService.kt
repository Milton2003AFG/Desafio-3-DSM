package com.example.desafio3dsm.network

import com.example.desafio3dsm.model.Recurso
import com.example.desafio3dsm.model.Usuario
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET("recursos")
    suspend fun getRecursos(): List<Recurso>

    @GET("recursos/{id}")
    suspend fun getRecursoById(@Path("id") id: String): Recurso

    @POST("recursos")
    suspend fun createRecurso(@Body recurso: Recurso): Recurso

    @PUT("recursos/{id}")
    suspend fun updateRecurso(@Path("id") id: String, @Body recurso: Recurso): Recurso

    @DELETE("recursos/{id}")
    suspend fun deleteRecurso(@Path("id") id: String): Recurso


    @GET("usuarios")
    suspend fun getUsuarios(): List<Usuario>

    @POST("usuarios")
    suspend fun createUsuario(@Body usuario: Usuario): Usuario

    @PUT("usuarios/{id}")
    suspend fun updateUsuario(@Path("id") id: String, @Body usuario: Usuario): Usuario
}