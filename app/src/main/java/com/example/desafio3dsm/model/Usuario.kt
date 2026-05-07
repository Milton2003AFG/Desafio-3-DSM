package com.example.desafio3dsm.model

data class Usuario(
    val id: String = "",
    val nombre: String = "",
    val email: String = "",
    val password: String = "",
    val rol: String = "",
    val favoritos: List<String> = emptyList()
)
