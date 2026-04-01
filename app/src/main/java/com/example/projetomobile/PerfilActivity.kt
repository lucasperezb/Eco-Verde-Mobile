package com.example.projetomobile

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

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
}

