package com.example.projetomobile

/**
 * Entidade que representa um produto disponível no app Eco Verde.
 * Campos:
 * - id: identificador único (Long, autoincrement no SQLite)
 * - nome: nome do produto (String)
 * - descricao: descrição breve do produto (String)
 * - preco: preço do produto em reais (Double)
 * - categoria: categoria do produto ex: "Vegetais", "Frutas", "Grãos" (String)
 * - estoque: quantidade disponível em estoque (Int)
 * - imagem_url: URL ou caminho da imagem (String, opcional)
 */
data class Product(
    var id: Long? = null,
    val nome: String,
    val descricao: String,
    val preco: Double,
    val categoria: String,
    val estoque: Int,
    val imagemUrl: String = ""
)

