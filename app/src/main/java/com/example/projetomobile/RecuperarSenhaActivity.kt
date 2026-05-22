package com.example.projetomobile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class RecuperarSenhaActivity : AppCompatActivity() {
    private enum class RecoveryStep {
        EMAIL,
        SECURITY,
        RESET
    }

    private var currentStep = RecoveryStep.EMAIL
    private var recoveryEmail: String = ""
    private lateinit var userDb: UserDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recuperar_senha)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainRecuperarSenha)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        userDb = UserDatabaseHelper(this)

        val voltarLoginIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        findViewById<TextView>(R.id.txtVoltarLoginRecuperacao).setOnClickListener {
            startActivity(voltarLoginIntent)
            finish()
        }

        updateUiForStep()
        findViewById<MaterialButton>(R.id.btnEnviarRecuperacao).setOnClickListener {
            when (currentStep) {
                RecoveryStep.EMAIL -> handleEmailStep()
                RecoveryStep.SECURITY -> handleSecurityStep()
                RecoveryStep.RESET -> handleResetStep(voltarLoginIntent)
            }
        }
    }

    private fun handleEmailStep() {
        val email = findViewById<TextInputEditText>(R.id.txtEmailRecuperacao).text.toString().trim()
        when {
            email.isEmpty() -> {
                findViewById<TextInputEditText>(R.id.txtEmailRecuperacao).error = "Campo obrigatório"
                Toast.makeText(this, "Por favor, preencha o email", Toast.LENGTH_LONG).show()
                return
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                findViewById<TextInputEditText>(R.id.txtEmailRecuperacao).error = "Email inválido"
                Toast.makeText(this, "Email inválido", Toast.LENGTH_LONG).show()
                return
            }
        }

        val question = userDb.getSecurityQuestionByEmail(email)
        if (question.isNullOrBlank()) {
            Toast.makeText(this, "Email não encontrado ou sem pergunta de segurança.", Toast.LENGTH_LONG).show()
            return
        }

        recoveryEmail = email
        findViewById<TextView>(R.id.txtPerguntaSegurancaRecuperacao).text =
            "Pergunta de segurança: $question"
        currentStep = RecoveryStep.SECURITY
        updateUiForStep()
    }

    private fun handleSecurityStep() {
        val answer = findViewById<TextInputEditText>(R.id.txtRespostaRecuperacao).text.toString().trim()
        if (answer.isEmpty()) {
            findViewById<TextInputEditText>(R.id.txtRespostaRecuperacao).error = "Campo obrigatório"
            Toast.makeText(this, "Por favor, preencha a resposta de segurança", Toast.LENGTH_LONG).show()
            return
        }

        val valid = userDb.validateSecurityAnswer(recoveryEmail, answer)
        if (!valid) {
            Toast.makeText(this, "Resposta de segurança incorreta.", Toast.LENGTH_LONG).show()
            return
        }

        currentStep = RecoveryStep.RESET
        updateUiForStep()
    }

    private fun handleResetStep(voltarLoginIntent: Intent) {
        val novaSenha = findViewById<TextInputEditText>(R.id.txtNovaSenhaRecuperacao).text.toString()
        val confirmar = findViewById<TextInputEditText>(R.id.txtConfirmarNovaSenhaRecuperacao).text.toString()

        when {
            novaSenha.isEmpty() -> {
                findViewById<TextInputEditText>(R.id.txtNovaSenhaRecuperacao).error = "Campo obrigatório"
                Toast.makeText(this, "Por favor, preencha a nova senha", Toast.LENGTH_LONG).show()
                return
            }
            novaSenha.length < 6 -> {
                findViewById<TextInputEditText>(R.id.txtNovaSenhaRecuperacao).error = "Mínimo 6 caracteres"
                Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres.", Toast.LENGTH_LONG).show()
                return
            }
            confirmar.isEmpty() -> {
                findViewById<TextInputEditText>(R.id.txtConfirmarNovaSenhaRecuperacao).error = "Campo obrigatório"
                Toast.makeText(this, "Por favor, confirme a nova senha", Toast.LENGTH_LONG).show()
                return
            }
            novaSenha != confirmar -> {
                findViewById<TextInputEditText>(R.id.txtConfirmarNovaSenhaRecuperacao).error = "Senhas não coincidem"
                Toast.makeText(this, "As senhas não coincidem.", Toast.LENGTH_LONG).show()
                return
            }
        }

        val updated = userDb.resetPasswordByEmail(recoveryEmail, novaSenha)
        if (updated) {
            Toast.makeText(this, "Senha atualizada com sucesso.", Toast.LENGTH_LONG).show()
            startActivity(voltarLoginIntent)
            finish()
        } else {
            Toast.makeText(this, "Não foi possível atualizar a senha.", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateUiForStep() {
        val tilResposta = findViewById<TextInputLayout>(R.id.tilRespostaRecuperacao)
        val tilNovaSenha = findViewById<TextInputLayout>(R.id.tilNovaSenhaRecuperacao)
        val tilConfirmarNovaSenha = findViewById<TextInputLayout>(R.id.tilConfirmarNovaSenhaRecuperacao)
        val txtPergunta = findViewById<TextView>(R.id.txtPerguntaSegurancaRecuperacao)
        val btn = findViewById<MaterialButton>(R.id.btnEnviarRecuperacao)
        val emailInput = findViewById<TextInputEditText>(R.id.txtEmailRecuperacao)

        when (currentStep) {
            RecoveryStep.EMAIL -> {
                txtPergunta.visibility = View.GONE
                tilResposta.visibility = View.GONE
                tilNovaSenha.visibility = View.GONE
                tilConfirmarNovaSenha.visibility = View.GONE
                emailInput.isEnabled = true
                btn.text = "Buscar pergunta"
            }
            RecoveryStep.SECURITY -> {
                txtPergunta.visibility = View.VISIBLE
                tilResposta.visibility = View.VISIBLE
                tilNovaSenha.visibility = View.GONE
                tilConfirmarNovaSenha.visibility = View.GONE
                emailInput.isEnabled = false
                btn.text = "Validar resposta"
            }
            RecoveryStep.RESET -> {
                txtPergunta.visibility = View.VISIBLE
                tilResposta.visibility = View.GONE
                tilNovaSenha.visibility = View.VISIBLE
                tilConfirmarNovaSenha.visibility = View.VISIBLE
                emailInput.isEnabled = false
                btn.text = "Atualizar senha"
            }
        }
    }
}
