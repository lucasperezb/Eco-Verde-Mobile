package com.example.projetomobile

import android.content.ContentValues
import android.content.Context

/**
 * DatabaseHelper específico para operações CRUD de Product.
 * Delegado pela classe DatabaseHelper que gerencia schema e versionamento.
 */
class ProductDatabaseHelper(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    /**
     * Insere um novo produto no banco de dados.
     * @param product Objeto Product a ser inserido
     * @return ID do produto inserido, ou -1 se falhar
     */
    fun insertProduct(product: Product): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.PRODUCT_COL_NOME, product.nome)
            put(DatabaseHelper.PRODUCT_COL_DESCRICAO, product.descricao)
            put(DatabaseHelper.PRODUCT_COL_PRECO, product.preco)
            put(DatabaseHelper.PRODUCT_COL_CATEGORIA, product.categoria)
            put(DatabaseHelper.PRODUCT_COL_ESTOQUE, product.estoque)
            put(DatabaseHelper.PRODUCT_COL_IMAGEM_URL, product.imagemUrl)
        }
        val id = db.insert(DatabaseHelper.TABLE_PRODUCT, null, values)
        product.id = if (id == -1L) null else id
        db.close()
        return id
    }

    /**
     * Obtém um produto pelo ID.
     * @param id ID do produto
     * @return Objeto Product ou null se não encontrado
     */
    fun getProduct(id: Long): Product? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_PRODUCT,
            arrayOf(
                DatabaseHelper.PRODUCT_COL_ID,
                DatabaseHelper.PRODUCT_COL_NOME,
                DatabaseHelper.PRODUCT_COL_DESCRICAO,
                DatabaseHelper.PRODUCT_COL_PRECO,
                DatabaseHelper.PRODUCT_COL_CATEGORIA,
                DatabaseHelper.PRODUCT_COL_ESTOQUE,
                DatabaseHelper.PRODUCT_COL_IMAGEM_URL
            ),
            "${DatabaseHelper.PRODUCT_COL_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        var product: Product? = null
        if (cursor.moveToFirst()) {
            val pid = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.PRODUCT_COL_ID))
            val nome = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PRODUCT_COL_NOME))
            val descricao = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PRODUCT_COL_DESCRICAO))
            val preco = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.PRODUCT_COL_PRECO))
            val categoria = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PRODUCT_COL_CATEGORIA))
            val estoque = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.PRODUCT_COL_ESTOQUE))
            val imagemUrl = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PRODUCT_COL_IMAGEM_URL))
            product = Product(pid, nome, descricao, preco, categoria, estoque, imagemUrl)
        }
        cursor.close()
        db.close()
        return product
    }

    /**
     * Obtém todos os produtos do banco.
     * @return Lista de produtos ordenados por nome
     */
    fun getAllProducts(): List<Product> {
        val products = mutableListOf<Product>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_PRODUCT,
            arrayOf(
                DatabaseHelper.PRODUCT_COL_ID,
                DatabaseHelper.PRODUCT_COL_NOME,
                DatabaseHelper.PRODUCT_COL_DESCRICAO,
                DatabaseHelper.PRODUCT_COL_PRECO,
                DatabaseHelper.PRODUCT_COL_CATEGORIA,
                DatabaseHelper.PRODUCT_COL_ESTOQUE,
                DatabaseHelper.PRODUCT_COL_IMAGEM_URL
            ),
            null,
            null,
            null,
            null,
            "${DatabaseHelper.PRODUCT_COL_NOME} ASC"
        )
        if (cursor.moveToFirst()) {
            do {
                val pid = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.PRODUCT_COL_ID))
                val nome = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PRODUCT_COL_NOME))
                val descricao = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PRODUCT_COL_DESCRICAO))
                val preco = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.PRODUCT_COL_PRECO))
                val categoria = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PRODUCT_COL_CATEGORIA))
                val estoque = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.PRODUCT_COL_ESTOQUE))
                val imagemUrl = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PRODUCT_COL_IMAGEM_URL))
                products.add(Product(pid, nome, descricao, preco, categoria, estoque, imagemUrl))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return products
    }

    /**
     * Obtém produtos filtrados por categoria.
     * @param categoria Nome da categoria
     * @return Lista de produtos da categoria, ordenados por nome
     */
    fun getProductsByCategory(categoria: String): List<Product> {
        val products = mutableListOf<Product>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_PRODUCT,
            arrayOf(
                DatabaseHelper.PRODUCT_COL_ID,
                DatabaseHelper.PRODUCT_COL_NOME,
                DatabaseHelper.PRODUCT_COL_DESCRICAO,
                DatabaseHelper.PRODUCT_COL_PRECO,
                DatabaseHelper.PRODUCT_COL_CATEGORIA,
                DatabaseHelper.PRODUCT_COL_ESTOQUE,
                DatabaseHelper.PRODUCT_COL_IMAGEM_URL
            ),
            "${DatabaseHelper.PRODUCT_COL_CATEGORIA} = ?",
            arrayOf(categoria),
            null,
            null,
            "${DatabaseHelper.PRODUCT_COL_NOME} ASC"
        )
        if (cursor.moveToFirst()) {
            do {
                val pid = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.PRODUCT_COL_ID))
                val nome = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PRODUCT_COL_NOME))
                val descricao = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PRODUCT_COL_DESCRICAO))
                val preco = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.PRODUCT_COL_PRECO))
                val cat = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PRODUCT_COL_CATEGORIA))
                val estoque = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.PRODUCT_COL_ESTOQUE))
                val imagemUrl = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PRODUCT_COL_IMAGEM_URL))
                products.add(Product(pid, nome, descricao, preco, cat, estoque, imagemUrl))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return products
    }

    /**
     * Atualiza os dados de um produto.
     * @param product Objeto Product com dados atualizados
     * @return Número de linhas afetadas
     */
    fun updateProduct(product: Product): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.PRODUCT_COL_NOME, product.nome)
            put(DatabaseHelper.PRODUCT_COL_DESCRICAO, product.descricao)
            put(DatabaseHelper.PRODUCT_COL_PRECO, product.preco)
            put(DatabaseHelper.PRODUCT_COL_CATEGORIA, product.categoria)
            put(DatabaseHelper.PRODUCT_COL_ESTOQUE, product.estoque)
            put(DatabaseHelper.PRODUCT_COL_IMAGEM_URL, product.imagemUrl)
        }
        val rowsAffected = db.update(
            DatabaseHelper.TABLE_PRODUCT,
            values,
            "${DatabaseHelper.PRODUCT_COL_ID} = ?",
            arrayOf(product.id.toString())
        )
        db.close()
        return rowsAffected
    }

    /**
     * Deleta um produto pelo ID.
     * @param id ID do produto a deletar
     * @return Número de linhas afetadas
     */
    fun deleteProduct(id: Long): Int {
        val db = dbHelper.writableDatabase
        val rowsAffected = db.delete(
            DatabaseHelper.TABLE_PRODUCT,
            "${DatabaseHelper.PRODUCT_COL_ID} = ?",
            arrayOf(id.toString())
        )
        db.close()
        return rowsAffected
    }

    /**
     * Obtém todas as categorias únicas de produtos.
     * @return Lista de categorias
     */
    fun getAllCategories(): List<String> {
        val categories = mutableListOf<String>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT DISTINCT ${DatabaseHelper.PRODUCT_COL_CATEGORIA} FROM ${DatabaseHelper.TABLE_PRODUCT} ORDER BY ${DatabaseHelper.PRODUCT_COL_CATEGORIA}",
            null
        )
        if (cursor.moveToFirst()) {
            do {
                val categoria = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PRODUCT_COL_CATEGORIA))
                categories.add(categoria)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return categories
    }
}

