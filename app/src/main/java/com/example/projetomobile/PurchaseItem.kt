package com.example.projetomobile

/**
 * Representa um item de compra (produto no pedido).
 */
data class PurchaseItem(
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val unitPrice: Double,
    val totalPrice: Double
)

