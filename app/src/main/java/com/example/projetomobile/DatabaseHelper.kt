@file:Suppress("unused")
package com.example.projetomobile

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        const val DATABASE_NAME = "eco_verde.db"
        const val DATABASE_VERSION = 1

        // Tabela users
        const val TABLE_USER = "users"
        const val USER_COL_ID = "id"
        const val USER_COL_NOME = "nome"
        const val USER_COL_EMAIL = "email"
        const val USER_COL_PASSWORD = "password"

        private const val SQL_CREATE_TABLE_USER =
            "CREATE TABLE $TABLE_USER (" +
                    "$USER_COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$USER_COL_NOME TEXT NOT NULL," +
                    "$USER_COL_EMAIL TEXT NOT NULL UNIQUE," +
                    "$USER_COL_PASSWORD TEXT NOT NULL" +
                    ")"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE_USER)
        
        // Criar usuário de teste para facilitar o desenvolvimento
        val values = ContentValues().apply {
            put(USER_COL_NOME, "Usuário Teste")
            put(USER_COL_EMAIL, "teste@example.com")
            put(USER_COL_PASSWORD, hashPassword("senha123"))
        }
        db.insert(TABLE_USER, null, values)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        onCreate(db)
    }

    // -------- Users CRUD simples (create + read) --------
    fun insertUser(user: User): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(USER_COL_NOME, user.nome)
            put(USER_COL_EMAIL, user.email)
            put(USER_COL_PASSWORD, hashPassword(user.password))
        }
        val id = db.insert(TABLE_USER, null, values)
        user.id = if (id == -1L) null else id
        db.close()
        return id
    }

    fun getUser(id: Long): User? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USER,
            arrayOf(USER_COL_ID, USER_COL_NOME, USER_COL_EMAIL, USER_COL_PASSWORD),
            "$USER_COL_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        var user: User? = null
        if (cursor.moveToFirst()) {
            val uid = cursor.getLong(cursor.getColumnIndexOrThrow(USER_COL_ID))
            val nome = cursor.getString(cursor.getColumnIndexOrThrow(USER_COL_NOME))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(USER_COL_EMAIL))
            val password = cursor.getString(cursor.getColumnIndexOrThrow(USER_COL_PASSWORD))
            user = User(uid, nome, email, password)
        }
        cursor.close()
        db.close()
        return user
    }

    fun getAllUsers(): List<User> {
        val users = mutableListOf<User>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USER,
            arrayOf(USER_COL_ID, USER_COL_NOME, USER_COL_EMAIL, USER_COL_PASSWORD),
            null,
            null,
            null,
            null,
            "$USER_COL_NOME ASC"
        )
        if (cursor.moveToFirst()) {
            do {
                val uid = cursor.getLong(cursor.getColumnIndexOrThrow(USER_COL_ID))
                val nome = cursor.getString(cursor.getColumnIndexOrThrow(USER_COL_NOME))
                val email = cursor.getString(cursor.getColumnIndexOrThrow(USER_COL_EMAIL))
                val password = cursor.getString(cursor.getColumnIndexOrThrow(USER_COL_PASSWORD))
                users.add(User(uid, nome, email, password))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return users
    }

    /**
     * Valida as credenciais do usuário (email e senha)
     * Retorna o usuário se as credenciais forem válidas, null caso contrário
     */
    fun authenticateUser(email: String, password: String): User? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USER,
            arrayOf(USER_COL_ID, USER_COL_NOME, USER_COL_EMAIL, USER_COL_PASSWORD),
            "$USER_COL_EMAIL = ?",
            arrayOf(email),
            null,
            null,
            null
        )
        
        var user: User? = null
        if (cursor.moveToFirst()) {
            val uid = cursor.getLong(cursor.getColumnIndexOrThrow(USER_COL_ID))
            val nome = cursor.getString(cursor.getColumnIndexOrThrow(USER_COL_NOME))
            val userEmail = cursor.getString(cursor.getColumnIndexOrThrow(USER_COL_EMAIL))
            val hashedPassword = cursor.getString(cursor.getColumnIndexOrThrow(USER_COL_PASSWORD))
            
            // Verificar se a senha corresponde à hash armazenada
            if (hashPassword(password) == hashedPassword) {
                user = User(uid, nome, userEmail, hashedPassword)
            }
        }
        cursor.close()
        db.close()
        return user
    }

    // Simple SHA-256 hash for password storage. In production use a stronger salted hash (e.g. bcrypt).
    private fun hashPassword(password: String): String {
        val md = java.security.MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(password.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}







