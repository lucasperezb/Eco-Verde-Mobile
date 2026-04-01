package com.example.projetomobile

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

class RecuperarSenhaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recuperar_senha)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainRecuperarSenha)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val voltarLoginIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        findViewById<TextView>(R.id.txtVoltarLoginRecuperacao).setOnClickListener {
            startActivity(voltarLoginIntent)
            finish()
        }

        findViewById<MaterialButton>(R.id.btnEnviarRecuperacao).setOnClickListener {
            startActivity(voltarLoginIntent)
            finish()
        }
    }
}
