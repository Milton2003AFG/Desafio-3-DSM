package com.example.desafio3dsm.model

data class Recurso(
    val id: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val tipo: String = "",
    val enlace: String = "",
    val imagen: String = "",
    val rating: Float = 0f,
    val totalRating: Int = 0
)
