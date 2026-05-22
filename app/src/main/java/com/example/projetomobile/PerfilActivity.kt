package com.example.projetomobile

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton

class PerfilActivity : AppCompatActivity() {

    private val sharedPrefsName = "eco_verde_prefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainPerfil)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Carregar dados do usuário logado
        carregarDadosUsuario()

        // Botão para ver minhas compras
        findViewById<MaterialButton>(R.id.btnMinhasCompras)?.setOnClickListener {
            val sharedPref = getSharedPreferences(sharedPrefsName, MODE_PRIVATE)
            val userId = sharedPref.getLong("user_id", -1L)
            if (userId != -1L) {
                mostrarMinhasCompras(userId)
            }
        }

        // Botão de alteração de senha
        findViewById<MaterialButton>(R.id.btnEditarPerfil).setOnClickListener {
            mostrarDialogoAlterarSenha()
        }

        findViewById<MaterialButton>(R.id.btnEditarEndereco).setOnClickListener {
            mostrarDialogoAlterarEndereco()
        }

        // Botão de exclusão da conta
        findViewById<MaterialButton>(R.id.btnExcluirConta).setOnClickListener {
            mostrarDialogoExcluirConta()
        }

        val bnvPerfil = findViewById<BottomNavigationView>(R.id.bnvPerfil)

        bnvPerfil.selectedItemId = R.id.menuPerfil
        bnvPerfil.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menuProdutos -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }

                R.id.menuCarrinho -> {
                    startActivity(Intent(this, CarrinhoActivity::class.java))
                    true
                }

                R.id.menuPerfil -> true
                else -> false
            }
        }
    }

    private fun carregarDadosUsuario() {
        val sharedPref = getSharedPreferences(sharedPrefsName, MODE_PRIVATE)
        val userId = sharedPref.getLong("user_id", -1L)

        if (userId != -1L) {
            try {
                val userDb = UserDatabaseHelper(this)
                val user = userDb.getUser(userId)

                if (user != null) {
                    // Exibir dados do usuário
                    findViewById<TextView>(R.id.txtNomePerfil).text = user.nome
                    findViewById<TextView>(R.id.txtEmailPerfil).text = user.email
                    val endereco = user.endereco.ifBlank { getString(R.string.endereco_nao_informado) }
                    findViewById<TextView>(R.id.txtInfoPerfil).text = getString(R.string.label_endereco, endereco)
                } else {
                    // Usuário não encontrado, fazer logout
                    limparSessaoEIrLogin()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Erro ao carregar perfil: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } else {
            // Nenhum usuário logado
            limparSessaoEIrLogin()
        }
    }

    private fun mostrarDialogoAlterarSenha() {
        val sharedPref = getSharedPreferences(sharedPrefsName, MODE_PRIVATE)
        val userId = sharedPref.getLong("user_id", -1L)

        if (userId == -1L) {
            limparSessaoEIrLogin()
            return
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 0)
        }

        val senhaAtualInput = EditText(this).apply {
            hint = getString(R.string.hint_senha_atual)
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        val novaSenhaInput = EditText(this).apply {
            hint = getString(R.string.hint_nova_senha)
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        val confirmarSenhaInput = EditText(this).apply {
            hint = getString(R.string.hint_confirmar_nova_senha)
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        layout.addView(senhaAtualInput)
        layout.addView(novaSenhaInput)
        layout.addView(confirmarSenhaInput)

        AlertDialog.Builder(this)
            .setTitle(R.string.titulo_alterar_senha)
            .setView(layout)
            .setPositiveButton(R.string.dialog_btn_salvar) { _, _ ->
                val senhaAtual = senhaAtualInput.text.toString()
                val novaSenha = novaSenhaInput.text.toString()
                val confirmarSenha = confirmarSenhaInput.text.toString()

                when {
                    senhaAtual.isBlank() -> {
                        Toast.makeText(this, R.string.erro_senha_atual_vazia, Toast.LENGTH_LONG).show()
                    }
                    novaSenha.length < 6 -> {
                        Toast.makeText(this, R.string.erro_senha_curta, Toast.LENGTH_LONG).show()
                    }
                    novaSenha != confirmarSenha -> {
                        Toast.makeText(this, R.string.erro_senha_diferente, Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        val result = UserDatabaseHelper(this).changePassword(userId, senhaAtual, novaSenha)
                        when (result) {
                            UserDatabaseHelper.PasswordChangeResult.SUCCESS -> {
                                Toast.makeText(this, R.string.msg_senha_atualizada, Toast.LENGTH_LONG).show()
                            }
                            UserDatabaseHelper.PasswordChangeResult.INVALID_CURRENT_PASSWORD -> {
                                Toast.makeText(this, R.string.erro_senha_atual_incorreta, Toast.LENGTH_LONG).show()
                            }
                            UserDatabaseHelper.PasswordChangeResult.USER_NOT_FOUND -> {
                                Toast.makeText(this, R.string.erro_usuario_nao_encontrado, Toast.LENGTH_LONG).show()
                                limparSessaoEIrLogin()
                            }
                            UserDatabaseHelper.PasswordChangeResult.UPDATE_FAILED -> {
                                Toast.makeText(this, R.string.erro_salvar_senha, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
            .setNegativeButton(R.string.dialog_btn_cancelar, null)
            .show()
    }

    private fun mostrarDialogoExcluirConta() {
        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_excluir_titulo)
            .setMessage(R.string.dialog_excluir_mensagem)
            .setPositiveButton(R.string.dialog_excluir_confirmar) { _, _ ->
                excluirContaAtual()
            }
            .setNegativeButton(R.string.dialog_excluir_cancelar, null)
            .show()
    }

    private fun mostrarDialogoAlterarEndereco() {
        val sharedPref = getSharedPreferences(sharedPrefsName, MODE_PRIVATE)
        val userId = sharedPref.getLong("user_id", -1L)
        if (userId == -1L) {
            limparSessaoEIrLogin()
            return
        }

        val userAtual = UserDatabaseHelper(this).getUser(userId)
        val input = EditText(this).apply {
            hint = getString(R.string.hint_endereco_perfil)
            setText(userAtual?.endereco ?: "")
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.titulo_alterar_endereco)
            .setView(input)
            .setPositiveButton(R.string.dialog_btn_salvar) { _, _ ->
                val novoEndereco = input.text.toString().trim()
                if (novoEndereco.isBlank()) {
                    Toast.makeText(this, R.string.erro_endereco_vazio, Toast.LENGTH_LONG).show()
                    return@setPositiveButton
                }

                val sucesso = UserDatabaseHelper(this).updateAddress(userId, novoEndereco)
                if (sucesso) {
                    findViewById<TextView>(R.id.txtInfoPerfil).text = getString(R.string.label_endereco, novoEndereco)
                    Toast.makeText(this, R.string.msg_endereco_atualizado, Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, R.string.erro_salvar_endereco, Toast.LENGTH_LONG).show()
                }
            }
            .setNegativeButton(R.string.dialog_btn_cancelar, null)
            .show()
    }

    private fun excluirContaAtual() {
        val sharedPref = getSharedPreferences(sharedPrefsName, MODE_PRIVATE)
        val userId = sharedPref.getLong("user_id", -1L)

        if (userId != -1L) {
            val rows = UserDatabaseHelper(this).deleteUser(userId)
            if (rows > 0) {
                Toast.makeText(this, R.string.msg_conta_excluida, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, R.string.erro_excluir_conta, Toast.LENGTH_LONG).show()
            }
        }

        limparSessaoEIrLogin()
    }

    private fun limparSessaoEIrLogin() {
        val sharedPref = getSharedPreferences(sharedPrefsName, MODE_PRIVATE)
        with(sharedPref.edit()) {
            remove("user_id")
            remove("user_name")
            remove("user_email")
            apply()
        }

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun mostrarMinhasCompras(userId: Long) {
        val purchaseDb = PurchaseDatabaseHelper(this)
        val compras = purchaseDb.getPurchasesByUserId(userId)

        if (compras.isEmpty()) {
            Toast.makeText(this, getString(R.string.nenhuma_compra), Toast.LENGTH_LONG).show()
            return
        }

        val items = compras.map { "PRO: ${it.protocolo} - R$ ${String.format("%.2f", it.total)}" }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle(R.string.titulo_minhas_compras)
            .setItems(items) { _, which ->
                val compra = compras[which]
                val intent = Intent(this, DetalheCompraActivity::class.java)
                intent.putExtra("PURCHASE_ID", compra.id)
                startActivity(intent)
            }
            .show()
    }
}
