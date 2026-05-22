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
        const val DATABASE_VERSION = 9

        // Tabela users
        const val TABLE_USER = "users"
        const val USER_COL_ID = "id"
        const val USER_COL_NOME = "nome"
        const val USER_COL_EMAIL = "email"
        const val USER_COL_PASSWORD = "password"
        const val USER_COL_ROLE = "role"
        const val USER_COL_ENDERECO = "endereco"
        const val USER_COL_SECURITY_QUESTION = "security_question"
        const val USER_COL_SECURITY_ANSWER = "security_answer"

        private const val SQL_CREATE_TABLE_USER =
            "CREATE TABLE $TABLE_USER (" +
                    "$USER_COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$USER_COL_NOME TEXT NOT NULL," +
                    "$USER_COL_EMAIL TEXT NOT NULL UNIQUE," +
                    "$USER_COL_PASSWORD TEXT NOT NULL," +
                    "$USER_COL_ROLE TEXT NOT NULL DEFAULT 'user'," +
                    "$USER_COL_ENDERECO TEXT NOT NULL DEFAULT ''," +
                    "$USER_COL_SECURITY_QUESTION TEXT NOT NULL DEFAULT ''," +
                    "$USER_COL_SECURITY_ANSWER TEXT NOT NULL DEFAULT ''" +
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

        // Tabela purchases
        const val TABLE_PURCHASE = "purchases"
        const val PURCHASE_COL_ID = "id"
        const val PURCHASE_COL_USER_ID = "user_id"
        const val PURCHASE_COL_PROTOCOLO = "protocolo"
        const val PURCHASE_COL_DATA = "data_compra"
        const val PURCHASE_COL_SUBTOTAL = "subtotal"
        const val PURCHASE_COL_FRETE = "frete"
        const val PURCHASE_COL_TOTAL = "total"
        const val PURCHASE_COL_PRODUTOS = "produtos"
        const val PURCHASE_COL_STATUS = "status"

        private const val SQL_CREATE_TABLE_PURCHASE =
            "CREATE TABLE $TABLE_PURCHASE (" +
                    "$PURCHASE_COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$PURCHASE_COL_USER_ID INTEGER NOT NULL," +
                    "$PURCHASE_COL_PROTOCOLO TEXT NOT NULL UNIQUE," +
                    "$PURCHASE_COL_DATA TEXT NOT NULL," +
                    "$PURCHASE_COL_SUBTOTAL REAL NOT NULL," +
                    "$PURCHASE_COL_FRETE REAL NOT NULL," +
                    "$PURCHASE_COL_TOTAL REAL NOT NULL," +
                    "$PURCHASE_COL_PRODUTOS TEXT NOT NULL," +
                    "$PURCHASE_COL_STATUS TEXT NOT NULL DEFAULT 'confirmada'," +
                    "FOREIGN KEY($PURCHASE_COL_USER_ID) REFERENCES $TABLE_USER($USER_COL_ID) ON DELETE CASCADE" +
                    ")"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE_USER)
        db.execSQL(SQL_CREATE_TABLE_PRODUCT)
        db.execSQL(SQL_CREATE_TABLE_PURCHASE)

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
            Product(nome = "Alface Hidropônica", descricao = "Alface fresca e crocante", preco = 6.90, categoria = "Vegetais", estoque = 50),
            Product(nome = "Tomate Cereja Orgânico", descricao = "Tomate vermelho e suculento", preco = 12.00, categoria = "Vegetais", estoque = 40),
            Product(nome = "Morango Fresco", descricao = "Morango doce e saboroso", preco = 10.00, categoria = "Frutas", estoque = 30),
            Product(nome = "Cenoura Orgânica", descricao = "Cenoura orgânica e nutritiva", preco = 5.50, categoria = "Vegetais", estoque = 60),
            Product(nome = "Maçã Vermelha", descricao = "Maçã vermelha e crocante", preco = 7.00, categoria = "Frutas", estoque = 45),
            Product(nome = "Brócolis Fresco", descricao = "Brócolis fresco e verde", preco = 12.00, categoria = "Vegetais", estoque = 25)
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
        if (oldVersion < 4) {
            db.execSQL(SQL_CREATE_TABLE_PURCHASE)
        }
        if (oldVersion < 3) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCT")
            db.execSQL(SQL_CREATE_TABLE_PRODUCT)
        }
        if (oldVersion < 7) {
            ensureUsersRoleColumn(db)
        }
        if (oldVersion < 8) {
            ensureUsersAddressColumn(db)
        }
        if (oldVersion < 9) {
            ensureUsersSecurityColumns(db)
        }
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        ensureUsersRoleColumn(db)
        ensureUsersAddressColumn(db)
        ensureUsersSecurityColumns(db)
    }

    private fun ensureUsersRoleColumn(db: SQLiteDatabase) {
        var hasRoleColumn = false
        val cursor = db.rawQuery("PRAGMA table_info($TABLE_USER)", null)
        try {
            val nameIndex = cursor.getColumnIndex("name")
            while (cursor.moveToNext()) {
                if (nameIndex >= 0 && cursor.getString(nameIndex) == USER_COL_ROLE) {
                    hasRoleColumn = true
                    break
                }
            }
        } finally {
            cursor.close()
        }

        if (!hasRoleColumn) {
            db.execSQL("ALTER TABLE $TABLE_USER ADD COLUMN $USER_COL_ROLE TEXT NOT NULL DEFAULT 'user'")
        }
    }

    private fun ensureUsersAddressColumn(db: SQLiteDatabase) {
        var hasAddressColumn = false
        val cursor = db.rawQuery("PRAGMA table_info($TABLE_USER)", null)
        try {
            val nameIndex = cursor.getColumnIndex("name")
            while (cursor.moveToNext()) {
                if (nameIndex >= 0 && cursor.getString(nameIndex) == USER_COL_ENDERECO) {
                    hasAddressColumn = true
                    break
                }
            }
        } finally {
            cursor.close()
        }

        if (!hasAddressColumn) {
            db.execSQL("ALTER TABLE $TABLE_USER ADD COLUMN $USER_COL_ENDERECO TEXT NOT NULL DEFAULT ''")
        }
    }

    private fun ensureUsersSecurityColumns(db: SQLiteDatabase) {
        var hasQuestionColumn = false
        var hasAnswerColumn = false
        val cursor = db.rawQuery("PRAGMA table_info($TABLE_USER)", null)
        try {
            val nameIndex = cursor.getColumnIndex("name")
            while (cursor.moveToNext()) {
                if (nameIndex < 0) continue
                when (cursor.getString(nameIndex)) {
                    USER_COL_SECURITY_QUESTION -> hasQuestionColumn = true
                    USER_COL_SECURITY_ANSWER -> hasAnswerColumn = true
                }
            }
        } finally {
            cursor.close()
        }

        if (!hasQuestionColumn) {
            db.execSQL("ALTER TABLE $TABLE_USER ADD COLUMN $USER_COL_SECURITY_QUESTION TEXT NOT NULL DEFAULT ''")
        }
        if (!hasAnswerColumn) {
            db.execSQL("ALTER TABLE $TABLE_USER ADD COLUMN $USER_COL_SECURITY_ANSWER TEXT NOT NULL DEFAULT ''")
        }
    }


    // Simple SHA-256 hash for password storage. In production use a stronger salted hash (e.g. bcrypt).
    private fun hashPassword(password: String): String {
        val md = java.security.MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(password.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}




