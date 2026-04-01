package com.example.projetomobile

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainHomeLoja)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<MaterialButton>(R.id.btnVerCarrinhoHome).setOnClickListener {
            startActivity(Intent(this, CarrinhoActivity::class.java))
        }

        findViewById<MaterialButton>(R.id.btnAdicionarCarrinhoAlface).setOnClickListener {
            startActivity(Intent(this, CarrinhoActivity::class.java))
        }

        findViewById<MaterialButton>(R.id.btnAdicionarCarrinhoTomate).setOnClickListener {
            startActivity(Intent(this, CarrinhoActivity::class.java))
        }

        findViewById<MaterialButton>(R.id.btnAdicionarCarrinhoMorango).setOnClickListener {
            startActivity(Intent(this, CarrinhoActivity::class.java))
        }

        val bnvHomeLoja = findViewById<BottomNavigationView>(R.id.bnvHomeLoja)
        bnvHomeLoja.selectedItemId = R.id.menuProdutos
        bnvHomeLoja.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menuProdutos -> true
                R.id.menuCarrinho -> {
                    startActivity(Intent(this, CarrinhoActivity::class.java))
                    true
                }

                R.id.menuPerfil -> {
                    startActivity(Intent(this, PerfilActivity::class.java))
                    true
                }

                else -> false
            }
        }
    }
}

