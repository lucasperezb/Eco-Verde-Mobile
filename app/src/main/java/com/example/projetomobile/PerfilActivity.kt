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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton

class PerfilActivity : AppCompatActivity() {

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

        // Botão de logout
        findViewById<MaterialButton>(R.id.btnExcluirConta).setOnClickListener {
            mostrarDialogoLogout()
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
        val sharedPref = getSharedPreferences("eco_verde_prefs", MODE_PRIVATE)
        val userId = sharedPref.getLong("user_id", -1L)

        if (userId != -1L) {
            try {
                val db = DatabaseHelper(this)
                val user = db.getUser(userId)

                if (user != null) {
                    // Exibir dados do usuário
                    findViewById<TextView>(R.id.txtNomePerfil).text = user.nome
                    findViewById<TextView>(R.id.txtEmailPerfil).text = user.email
                    findViewById<TextView>(R.id.txtInfoPerfil).text = "ID do usuário: ${user.id}"
                } else {
                    // Usuário não encontrado, fazer logout
                    fazer_logout()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Erro ao carregar perfil: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } else {
            // Nenhum usuário logado
            fazer_logout()
        }
    }

    private fun mostrarDialogoLogout() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Tem certeza que deseja sair?")
            .setPositiveButton("Sim") { _, _ ->
                fazer_logout()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun fazer_logout() {
        // Limpar dados do usuário
        val sharedPref = getSharedPreferences("eco_verde_prefs", MODE_PRIVATE)
        with(sharedPref.edit()) {
            remove("user_id")
            remove("user_name")
            remove("user_email")
            apply()
        }

        // Voltar para MainActivity
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}


