@file:Suppress("unused")
package com.example.projetomobile

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Gerencia a criação e versionamento do banco de dados.
 * As operações CRUD são delegadas para DatabaseHelpers específicas de cada entidade.
 */
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        const val DATABASE_NAME = "eco_verde.db"
        // bump version because schema changed (added role column)
        const val DATABASE_VERSION = 2

        // Tabela users
        const val TABLE_USER = "users"
        const val USER_COL_ID = "id"
        const val USER_COL_NOME = "nome"
        const val USER_COL_EMAIL = "email"
        const val USER_COL_PASSWORD = "password"
        const val USER_COL_ROLE = "role"

        private const val SQL_CREATE_TABLE_USER =
            "CREATE TABLE $TABLE_USER (" +
                    "$USER_COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$USER_COL_NOME TEXT NOT NULL," +
                    "$USER_COL_EMAIL TEXT NOT NULL UNIQUE," +
                    "$USER_COL_PASSWORD TEXT NOT NULL," +
                    "$USER_COL_ROLE TEXT NOT NULL DEFAULT 'user'" +
                    ")"

        // Tabela products
        const val TABLE_PRODUCT = "products"
        const val PRODUCT_COL_ID = "id"
        const val PRODUCT_COL_NOME = "nome"
        const val PRODUCT_COL_DESCRICAO = "descricao"
        const val PRODUCT_COL_PRECO = "preco"
        const val PRODUCT_COL_CATEGORIA = "categoria"
        const val PRODUCT_COL_ESTOQUE = "estoque"
        const val PRODUCT_COL_IMAGEM_URL = "imagem_url"

        private const val SQL_CREATE_TABLE_PRODUCT =
            "CREATE TABLE $TABLE_PRODUCT (" +
                    "$PRODUCT_COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$PRODUCT_COL_NOME TEXT NOT NULL," +
                    "$PRODUCT_COL_DESCRICAO TEXT NOT NULL," +
                    "$PRODUCT_COL_PRECO REAL NOT NULL," +
                    "$PRODUCT_COL_CATEGORIA TEXT NOT NULL," +
                    "$PRODUCT_COL_ESTOQUE INTEGER NOT NULL," +
                    "$PRODUCT_COL_IMAGEM_URL TEXT" +
                    ")"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE_USER)
        db.execSQL(SQL_CREATE_TABLE_PRODUCT)

        // Criar usuário de teste para facilitar o desenvolvimento
        val userValues = ContentValues().apply {
            put(USER_COL_NOME, "Usuário Teste")
            put(USER_COL_EMAIL, "teste@example.com")
            put(USER_COL_PASSWORD, hashPassword("senha123"))
            put(USER_COL_ROLE, "user")
        }
        db.insert(TABLE_USER, null, userValues)

        // Criar produtos de teste
        val produtos = listOf(
            Product(nome = "Alface", descricao = "Alface fresca e crocante", preco = 6.90, categoria = "Vegetais", estoque = 50),
            Product(nome = "Tomate", descricao = "Tomate vermelho e suculento", preco = 8.50, categoria = "Vegetais", estoque = 40),
            Product(nome = "Morango", descricao = "Morango doce e saboroso", preco = 10.00, categoria = "Frutas", estoque = 30),
            Product(nome = "Cenoura", descricao = "Cenoura orgânica e nutritiva", preco = 5.50, categoria = "Vegetais", estoque = 60),
            Product(nome = "Maçã", descricao = "Maçã vermelha e crocante", preco = 7.00, categoria = "Frutas", estoque = 45),
            Product(nome = "Brócolis", descricao = "Brócolis fresco e verde", preco = 12.00, categoria = "Vegetais", estoque = 25)
        )
        
        for (produto in produtos) {
            val productValues = ContentValues().apply {
                put(PRODUCT_COL_NOME, produto.nome)
                put(PRODUCT_COL_DESCRICAO, produto.descricao)
                put(PRODUCT_COL_PRECO, produto.preco)
                put(PRODUCT_COL_CATEGORIA, produto.categoria)
                put(PRODUCT_COL_ESTOQUE, produto.estoque)
                put(PRODUCT_COL_IMAGEM_URL, produto.imagemUrl)
            }
            db.insert(TABLE_PRODUCT, null, productValues)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCT")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        onCreate(db)
    }


    // Simple SHA-256 hash for password storage. In production use a stronger salted hash (e.g. bcrypt).
    private fun hashPassword(password: String): String {
        val md = java.security.MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(password.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}







