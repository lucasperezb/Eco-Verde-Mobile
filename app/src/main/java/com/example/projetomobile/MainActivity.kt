package com.example.projetomobile

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<MaterialButton>(R.id.btnEntrar).setOnClickListener {
            validarLogin()
        }

        findViewById<TextView>(R.id.txtCriarConta).setOnClickListener {
            startActivity(Intent(this, CadastroActivity::class.java))
        }

        findViewById<TextView>(R.id.txtEsqueciSenha).setOnClickListener {
            startActivity(Intent(this, RecuperarSenhaActivity::class.java))
        }
    }

    private fun validarLogin() {
        val email = findViewById<TextInputEditText>(R.id.txtEmail).text.toString().trim()
        val senha = findViewById<TextInputEditText>(R.id.txtSenha).text.toString()

        // Validações
        var erroValidacao = false

        when {
            email.isEmpty() -> {
                findViewById<TextInputEditText>(R.id.txtEmail).error = "Campo obrigatório"
                Toast.makeText(this, "Por favor, preencha o email", Toast.LENGTH_LONG).show()
                erroValidacao = true
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                findViewById<TextInputEditText>(R.id.txtEmail).error = "Email inválido"
                Toast.makeText(this, "Email inválido", Toast.LENGTH_LONG).show()
                erroValidacao = true
            }
            senha.isEmpty() -> {
                findViewById<TextInputEditText>(R.id.txtSenha).error = "Campo obrigatório"
                Toast.makeText(this, "Por favor, preencha a senha", Toast.LENGTH_LONG).show()
                erroValidacao = true
            }
        }

        if (!erroValidacao) {
            try {
                val userDb = UserDatabaseHelper(this)
                val user = userDb.authenticateUser(email, senha)

                if (user != null) {
                    // Login bem-sucedido
                    // Salvar ID do usuário em SharedPreferences
                    val sharedPref = getSharedPreferences("eco_verde_prefs", MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putLong("user_id", user.id ?: -1L)
                        putString("user_name", user.nome)
                        putString("user_email", user.email)
                        apply()
                    }
                    
                    Toast.makeText(this, "Bem-vindo, ${user.nome}!", Toast.LENGTH_LONG).show()
                    // Navegar para HomeActivity ou AdminActivity conforme role
                    if (user.role == "admin") {
                        startActivity(Intent(this, AdminActivity::class.java))
                    } else {
                        startActivity(Intent(this, HomeActivity::class.java))
                    }
                    finish()
                } else {
                    // Email ou senha incorretos
                    Toast.makeText(this, "Email ou senha incorretos", Toast.LENGTH_LONG).show()
                    findViewById<TextInputEditText>(R.id.txtSenha).setText("")
                    findViewById<TextInputEditText>(R.id.txtSenha).requestFocus()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Erro ao fazer login: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}