package com.example.projetomobile

import android.content.ContentValues
import android.content.Context

/**
 * DatabaseHelper específico para operações CRUD de User.
 * Delegado pela classe DatabaseHelper que gerencia schema e versionamento.
 */
class UserDatabaseHelper(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    /**
     * Insere um novo usuário no banco de dados.
     * @param user Objeto User a ser inserido
     * @return ID do usuário inserido, ou -1 se falhar
     */
    fun insertUser(user: User): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.USER_COL_NOME, user.nome)
            put(DatabaseHelper.USER_COL_EMAIL, user.email)
            put(DatabaseHelper.USER_COL_PASSWORD, hashPassword(user.password))
            put(DatabaseHelper.USER_COL_ROLE, user.role)
        }
        val id = db.insert(DatabaseHelper.TABLE_USER, null, values)
        user.id = if (id == -1L) null else id
        db.close()
        return id
    }

    /**
     * Obtém um usuário pelo ID.
     * @param id ID do usuário
     * @return Objeto User ou null se não encontrado
     */
    fun getUser(id: Long): User? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_USER,
            arrayOf(
                DatabaseHelper.USER_COL_ID,
                DatabaseHelper.USER_COL_NOME,
                DatabaseHelper.USER_COL_EMAIL,
                DatabaseHelper.USER_COL_PASSWORD,
                DatabaseHelper.USER_COL_ROLE
            ),
            "${DatabaseHelper.USER_COL_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        var user: User? = null
        if (cursor.moveToFirst()) {
            val uid = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_COL_ID))
            val nome = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_COL_NOME))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_COL_EMAIL))
            val password = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_COL_PASSWORD))
            val role = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_COL_ROLE))
            user = User(uid, nome, email, password, role)
        }
        cursor.close()
        db.close()
        return user
    }

    /**
     * Obtém todos os usuários do banco.
     * @return Lista de usuários ordenados por nome
     */
    fun getAllUsers(): List<User> {
        val users = mutableListOf<User>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_USER,
            arrayOf(
                DatabaseHelper.USER_COL_ID,
                DatabaseHelper.USER_COL_NOME,
                DatabaseHelper.USER_COL_EMAIL,
                DatabaseHelper.USER_COL_PASSWORD,
                DatabaseHelper.USER_COL_ROLE
            ),
            null,
            null,
            null,
            null,
            "${DatabaseHelper.USER_COL_NOME} ASC"
        )
        if (cursor.moveToFirst()) {
            do {
                val uid = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_COL_ID))
                val nome = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_COL_NOME))
                val email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_COL_EMAIL))
                val password = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_COL_PASSWORD))
                val role = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_COL_ROLE))
                users.add(User(uid, nome, email, password, role))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return users
    }

    /**
     * Autentica um usuário validando email e senha.
     * @param email Email do usuário
     * @param password Senha em texto plano
     * @return Objeto User se autenticado com sucesso, null caso contrário
     */
    fun authenticateUser(email: String, password: String): User? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_USER,
            arrayOf(
                DatabaseHelper.USER_COL_ID,
                DatabaseHelper.USER_COL_NOME,
                DatabaseHelper.USER_COL_EMAIL,
                DatabaseHelper.USER_COL_PASSWORD,
                DatabaseHelper.USER_COL_ROLE
            ),
            "${DatabaseHelper.USER_COL_EMAIL} = ?",
            arrayOf(email),
            null,
            null,
            null
        )

        var user: User? = null
        if (cursor.moveToFirst()) {
            val uid = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_COL_ID))
            val nome = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_COL_NOME))
            val userEmail = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_COL_EMAIL))
            val hashedPassword = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_COL_PASSWORD))
            val role = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_COL_ROLE))

            // Verificar se a senha corresponde à hash armazenada
            if (hashPassword(password) == hashedPassword) {
                user = User(uid, nome, userEmail, hashedPassword, role)
            }
        }
        cursor.close()
        db.close()
        return user
    }

    /**
     * Atualiza os dados de um usuário.
     * @param user Objeto User com dados atualizados
     * @return Número de linhas afetadas
     */
    fun updateUser(user: User): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.USER_COL_NOME, user.nome)
            put(DatabaseHelper.USER_COL_EMAIL, user.email)
            put(DatabaseHelper.USER_COL_PASSWORD, hashPassword(user.password))
            put(DatabaseHelper.USER_COL_ROLE, user.role)
        }
        val rowsAffected = db.update(
            DatabaseHelper.TABLE_USER,
            values,
            "${DatabaseHelper.USER_COL_ID} = ?",
            arrayOf(user.id.toString())
        )
        db.close()
        return rowsAffected
    }

    /**
     * Deleta um usuário pelo ID.
     * @param id ID do usuário a deletar
     * @return Número de linhas afetadas
     */
    fun deleteUser(id: Long): Int {
        val db = dbHelper.writableDatabase
        val rowsAffected = db.delete(
            DatabaseHelper.TABLE_USER,
            "${DatabaseHelper.USER_COL_ID} = ?",
            arrayOf(id.toString())
        )
        db.close()
        return rowsAffected
    }

    // Simple SHA-256 hash for password storage
    private fun hashPassword(password: String): String {
        val md = java.security.MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(password.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}



