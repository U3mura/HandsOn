package com.example.handson.model

data class Tutorial(
    var id: String? = null,
    var nome: String,
    var des: String,
    var salvo: Boolean = false,
    var idUsuario: String? = null
)
