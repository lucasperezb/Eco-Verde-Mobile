package com.example.projetomobile

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

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
            startActivity(Intent(this, HomeActivity::class.java))
        }

        findViewById<TextView>(R.id.txtCriarConta).setOnClickListener {
            startActivity(Intent(this, CadastroActivity::class.java))
        }

        findViewById<TextView>(R.id.txtEsqueciSenha).setOnClickListener {
            startActivity(Intent(this, RecuperarSenhaActivity::class.java))
        }
    }
}