package com.example.projetomobile

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class CadastroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cadastro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainCadastro)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<TextView>(R.id.txtVoltarLogin).setOnClickListener {
            finish()//fecha tela atual
        }

        findViewById<MaterialButton>(R.id.btnCriarContaCadastro).setOnClickListener {
            val nome = findViewById<TextInputEditText>(R.id.txtNome).text.toString().trim()
            val email = findViewById<TextInputEditText>(R.id.txtEmailCadastro).text.toString().trim()
            val senha = findViewById<TextInputEditText>(R.id.txtSenhaCadastro).text.toString()
            val confirmaSenha = findViewById<TextInputEditText>(R.id.txtConfirmarSenha).text.toString()

            // Validações
            var erroValidacao = false
            
            when {
                nome.isEmpty() -> {
                    findViewById<TextInputEditText>(R.id.txtNome).error = "Campo obrigatório"
                    Toast.makeText(this, "Por favor, preencha o nome", Toast.LENGTH_LONG).show()
                    erroValidacao = true
                }
                email.isEmpty() -> {
                    findViewById<TextInputEditText>(R.id.txtEmailCadastro).error = "Campo obrigatório"
                    Toast.makeText(this, "Por favor, preencha o email", Toast.LENGTH_LONG).show()
                    erroValidacao = true
                }
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    findViewById<TextInputEditText>(R.id.txtEmailCadastro).error = "Email inválido"
                    Toast.makeText(this, "Email inválido", Toast.LENGTH_LONG).show()
                    erroValidacao = true
                }
                senha.isEmpty() -> {
                    findViewById<TextInputEditText>(R.id.txtSenhaCadastro).error = "Campo obrigatório"
                    Toast.makeText(this, "Por favor, preencha a senha", Toast.LENGTH_LONG).show()
                    erroValidacao = true
                }
                senha.length < 6 -> {
                    findViewById<TextInputEditText>(R.id.txtSenhaCadastro).error = "Mínimo 6 caracteres"
                    Toast.makeText(this, "Senha deve ter pelo menos 6 caracteres", Toast.LENGTH_LONG).show()
                    erroValidacao = true
                }
                confirmaSenha.isEmpty() -> {
                    findViewById<TextInputEditText>(R.id.txtConfirmarSenha).error = "Campo obrigatório"
                    Toast.makeText(this, "Por favor, confirme a senha", Toast.LENGTH_LONG).show()
                    erroValidacao = true
                }
                senha != confirmaSenha -> {
                    findViewById<TextInputEditText>(R.id.txtConfirmarSenha).error = "Senhas não coincidem"
                    Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_LONG).show()
                    erroValidacao = true
                }
            }
            
            // Se não houver erros, criar conta
            if (!erroValidacao) {
                try {
                    // Perguntar se deseja criar conta de administrador
                    AlertDialog.Builder(this)
                        .setTitle("Tipo de conta")
                        .setMessage("Deseja criar uma conta de administrador?")
                        .setPositiveButton("Sim") { _, _ ->
                            // Solicitar código secreto para criar admin
                            val input = android.widget.EditText(this)
                            input.hint = "Código de administrador"
                            AlertDialog.Builder(this)
                                .setTitle("Código de administrador")
                                .setView(input)
                                .setPositiveButton("OK") { _, _ ->
                                    val codigo = input.text.toString().trim()
                                    if (codigo == "SUPERADMIN2026") {
                                        val userDb = UserDatabaseHelper(this)
                                        val novoUser = User(nome = nome, email = email, password = senha, role = "admin")
                                        val id = userDb.insertUser(novoUser)
                                        if (id != -1L) {
                                            AlertDialog.Builder(this)
                                                .setTitle("Sucesso!")
                                                .setMessage("Conta de administrador criada com sucesso. Faça login.")
                                                .setPositiveButton("OK") { _, _ ->
                                                    startActivity(Intent(this, MainActivity::class.java))
                                                    finish()
                                                }
                                                .setCancelable(false)
                                                .show()
                                        } else {
                                            Toast.makeText(this, "Erro ao criar conta. Email pode estar duplicado.", Toast.LENGTH_LONG).show()
                                        }
                                    } else {
                                        Toast.makeText(this, "Código inválido. Conta de administrador não criada.", Toast.LENGTH_LONG).show()
                                    }
                                }
                                .setNegativeButton("Cancelar", null)
                                .show()
                        }
                        .setNegativeButton("Não") { _, _ ->
                            try {
                                val userDb = UserDatabaseHelper(this)
                                val novoUser = User(nome = nome, email = email, password = senha)
                                val id = userDb.insertUser(novoUser)
                                if (id != -1L) {
                                    // Mostrar AlertDialog de sucesso
                                    AlertDialog.Builder(this)
                                        .setTitle("Sucesso!")
                                        .setMessage("Conta criada com sucesso! Agora faça login com suas credenciais.")
                                        .setPositiveButton("OK") { _, _ ->
                                            // Navegar para MainActivity (tela de login)
                                            startActivity(Intent(this, MainActivity::class.java))
                                            finish()
                                        }
                                        .setCancelable(false)
                                        .show()
                                } else {
                                    Toast.makeText(this, "Erro ao criar conta. Email pode estar duplicado.", Toast.LENGTH_LONG).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(this, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                        .show()
                } catch (e: Exception) {
                    Toast.makeText(this, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
