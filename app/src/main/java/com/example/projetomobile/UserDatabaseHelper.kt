package com.example.projetomobile

import android.content.ContentValues
import android.content.Context

/**
 * DatabaseHelper específico para operações CRUD de User.
 * Delegado pela classe DatabaseHelper que gerencia schema e versionamento.
 */
class UserDatabaseHelper(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    enum class PasswordChangeResult {
        SUCCESS,
        USER_NOT_FOUND,
        INVALID_CURRENT_PASSWORD,
        UPDATE_FAILED
    }

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
            put(DatabaseHelper.USER_COL_ENDERECO, user.endereco)
            put(DatabaseHelper.USER_COL_SECURITY_QUESTION, user.securityQuestion)
            put(DatabaseHelper.USER_COL_SECURITY_ANSWER, normalizeSecurityAnswer(user.securityAnswer))
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
                DatabaseHelper.USER_COL_ROLE,
                DatabaseHelper.USER_COL_ENDERECO,
                DatabaseHelper.USER_COL_SECURITY_QUESTION,
                DatabaseHelper.USER_COL_SECURITY_ANSWER
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
            val endereco = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_COL_ENDERECO))
            val securityQuestion = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_COL_SECURITY_QUESTION))
            val securityAnswer = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_COL_SECURITY_ANSWER))
            user = User(
                id = uid,
                nome = nome,
                email = email,
                password = password,
                endereco = endereco,
                securityQuestion = securityQuestion,
                securityAnswer = securityAnswer,
                role = role
            )
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
                DatabaseHelper.USER_COL_ROLE,
                DatabaseHelper.USER_COL_ENDERECO,
                DatabaseHelper.USER_COL_SECURITY_QUESTION,
                DatabaseHelper.USER_COL_SECURITY_ANSWER
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
                val endereco = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_COL_ENDERECO))
                val securityQuestion = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_COL_SECURITY_QUESTION))
                val securityAnswer = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_COL_SECURITY_ANSWER))
                users.add(
                    User(
                        id = uid,
                        nome = nome,
                        email = email,
                        password = password,
                        endereco = endereco,
                        securityQuestion = securityQuestion,
                        securityAnswer = securityAnswer,
                        role = role
                    )
                )
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
                DatabaseHelper.USER_COL_ROLE,
                DatabaseHelper.USER_COL_ENDERECO,
                DatabaseHelper.USER_COL_SECURITY_QUESTION,
                DatabaseHelper.USER_COL_SECURITY_ANSWER
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
            val storedPassword = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_COL_PASSWORD))
            val role = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_COL_ROLE))
            val endereco = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_COL_ENDERECO))
            val securityQuestion = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_COL_SECURITY_QUESTION))
            val securityAnswer = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_COL_SECURITY_ANSWER))

            // Compatibilidade: aceita senha em hash e também legado em texto puro.
            val inputPasswordHash = hashPassword(password)
            val isPasswordValid = (inputPasswordHash == storedPassword) || (password == storedPassword)

            if (isPasswordValid) {
                // Migra automaticamente senhas legadas (texto puro) para hash no primeiro login.
                if (password == storedPassword) {
                    val values = ContentValues().apply {
                        put(DatabaseHelper.USER_COL_PASSWORD, inputPasswordHash)
                    }
                    db.update(
                        DatabaseHelper.TABLE_USER,
                        values,
                        "${DatabaseHelper.USER_COL_ID} = ?",
                        arrayOf(uid.toString())
                    )
                }
                user = User(
                    id = uid,
                    nome = nome,
                    email = userEmail,
                    password = inputPasswordHash,
                    endereco = endereco,
                    securityQuestion = securityQuestion,
                    securityAnswer = securityAnswer,
                    role = role
                )
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
            put(DatabaseHelper.USER_COL_ENDERECO, user.endereco)
            put(DatabaseHelper.USER_COL_SECURITY_QUESTION, user.securityQuestion)
            put(DatabaseHelper.USER_COL_SECURITY_ANSWER, normalizeSecurityAnswer(user.securityAnswer))
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
     * Atualiza somente a senha do usuário, validando a senha atual.
     * @param userId ID do usuário
     * @param currentPassword Senha atual em texto plano
     * @param newPassword Nova senha em texto plano
     * @return Resultado da operação
     */
    fun changePassword(userId: Long, currentPassword: String, newPassword: String): PasswordChangeResult {
        val db = dbHelper.writableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_USER,
            arrayOf(DatabaseHelper.USER_COL_PASSWORD),
            "${DatabaseHelper.USER_COL_ID} = ?",
            arrayOf(userId.toString()),
            null,
            null,
            null
        )

        try {
            if (!cursor.moveToFirst()) {
                return PasswordChangeResult.USER_NOT_FOUND
            }

            val storedPassword = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_COL_PASSWORD))
            if (hashPassword(currentPassword) != storedPassword) {
                return PasswordChangeResult.INVALID_CURRENT_PASSWORD
            }

            val values = ContentValues().apply {
                put(DatabaseHelper.USER_COL_PASSWORD, hashPassword(newPassword))
            }

            val rowsAffected = db.update(
                DatabaseHelper.TABLE_USER,
                values,
                "${DatabaseHelper.USER_COL_ID} = ?",
                arrayOf(userId.toString())
            )

            return if (rowsAffected > 0) {
                PasswordChangeResult.SUCCESS
            } else {
                PasswordChangeResult.UPDATE_FAILED
            }
        } finally {
            cursor.close()
            db.close()
        }
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

    fun updateAddress(userId: Long, endereco: String): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.USER_COL_ENDERECO, endereco)
        }
        val rows = db.update(
            DatabaseHelper.TABLE_USER,
            values,
            "${DatabaseHelper.USER_COL_ID} = ?",
            arrayOf(userId.toString())
        )
        db.close()
        return rows > 0
    }

    fun getSecurityQuestionByEmail(email: String): String? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_USER,
            arrayOf(DatabaseHelper.USER_COL_SECURITY_QUESTION),
            "${DatabaseHelper.USER_COL_EMAIL} = ?",
            arrayOf(email.trim()),
            null,
            null,
            null
        )
        val question = if (cursor.moveToFirst()) {
            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_COL_SECURITY_QUESTION))
        } else {
            null
        }
        cursor.close()
        db.close()
        return question
    }

    fun validateSecurityAnswer(email: String, answer: String): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_USER,
            arrayOf(DatabaseHelper.USER_COL_SECURITY_ANSWER),
            "${DatabaseHelper.USER_COL_EMAIL} = ?",
            arrayOf(email.trim()),
            null,
            null,
            null
        )
        val isValid = if (cursor.moveToFirst()) {
            val storedAnswer = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_COL_SECURITY_ANSWER))
            storedAnswer == normalizeSecurityAnswer(answer)
        } else {
            false
        }
        cursor.close()
        db.close()
        return isValid
    }

    fun resetPasswordByEmail(email: String, newPassword: String): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.USER_COL_PASSWORD, hashPassword(newPassword))
        }
        val rows = db.update(
            DatabaseHelper.TABLE_USER,
            values,
            "${DatabaseHelper.USER_COL_EMAIL} = ?",
            arrayOf(email.trim())
        )
        db.close()
        return rows > 0
    }

    // Simple SHA-256 hash for password storage
    private fun hashPassword(password: String): String {
        val md = java.security.MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(password.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun normalizeSecurityAnswer(answer: String): String {
        return answer.trim().lowercase()
    }
}
