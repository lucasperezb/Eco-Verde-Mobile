package com.example.projetomobile

/**
 * Entidade que representa uma compra realizada no app Eco Verde.
 * Campos:
 * - id: identificador único (Long, autoincrement no SQLite)
 * - userId: ID do usuário que fez a compra (Long, FK para users)
 * - protocolo: número de protocolo único (String)
 * - dataCompra: data/hora da compra (String, ISO 8601)
 * - subtotal: valor dos produtos (Double)
 * - frete: valor do frete (Double)
 * - total: valor total da compra (Double)
 * - produtos: lista de produtos com quantidades (String JSON serializado)
 * - status: status da compra ex: "pendente", "confirmada" (String)
 */
data class Purchase(
    var id: Long? = null,
    val userId: Long,
    val protocolo: String,
    val dataCompra: String,
    val subtotal: Double,
    val frete: Double,
    val total: Double,
    val produtos: String = "",  // JSON serializado
    val status: String = "confirmada"
)

