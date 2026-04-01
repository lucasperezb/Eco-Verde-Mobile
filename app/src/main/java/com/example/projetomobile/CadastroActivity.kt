package com.example.projetomobile

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

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
            startActivity(Intent(this, HomeActivity::class.java)) //this se refere a tela atual, e HomeActivity é a tela para onde queremos ir
        }
    }
}
