package com.example.projetomobile

/**
 * Entidade que representa um usuário do aplicativo Eco Verde.
 * Campos mínimos:
 * - id: identificador único (Long, autoincrement no SQLite)
 * - nome: nome do usuário (String)
 * - email: email do usuário (String)
 * - password: senha do usuário (String) - será armazenada como hash
 */
data class User(
    var id: Long? = null,
    val nome: String,
    val email: String,
    val password: String
)


