package com.example.parcialkotlinapp.models


data class CrearUsuarioRequest(
    val nombre: String,
    val apellido: String,
    val correo: String,
    val password: String,
             // Calculada automáticamente desde el DatePicker
    val universidad: String, // Nuevo campo requerido
    val semestre: Int,

)