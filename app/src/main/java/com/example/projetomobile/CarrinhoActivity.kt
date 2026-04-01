package com.example.projetomobile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import java.text.NumberFormat
import java.util.Locale

class CarrinhoActivity : AppCompatActivity() {
    private val precoAlface = 6.90
    private val precoMorango = 10.00
    private val freteBase = 12.00

    private var subtotalAtual = 0.0
    private var alfaceRemovido = false
    private var morangoRemovido = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_carrinho)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainCarrinho)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val cardItemCarrinhoAlface = findViewById<MaterialCardView>(R.id.cardItemCarrinhoAlface) //referencia o card do item alface o findViewById é um metodo que procura um view no layout pelo ID
        val cardItemCarrinhoMorango = findViewById<MaterialCardView>(R.id.cardItemCarrinhoMorango)
        val txtSubtotalCarrinho = findViewById<TextView>(R.id.txtSubtotalCarrinho)
        val txtFreteCarrinho = findViewById<TextView>(R.id.txtFreteCarrinho)
        val txtTotalCarrinho = findViewById<TextView>(R.id.txtTotalCarrinho)

        subtotalAtual = precoAlface + precoMorango
        atualizarResumo(txtSubtotalCarrinho, txtFreteCarrinho, txtTotalCarrinho)

        findViewById<MaterialButton>(R.id.btnRemoverItemCarrinhoAlface).setOnClickListener { // essa funcao e chamada quando o botao for clicado
            if (!alfaceRemovido) {
                alfaceRemovido = true
                subtotalAtual -= precoAlface
                cardItemCarrinhoAlface.visibility = View.GONE
                atualizarResumo(txtSubtotalCarrinho, txtFreteCarrinho, txtTotalCarrinho)
            }
        }

        findViewById<MaterialButton>(R.id.btnRemoverItemCarrinhoMorango).setOnClickListener {
            if (!morangoRemovido) {
                morangoRemovido = true
                subtotalAtual -= precoMorango
                cardItemCarrinhoMorango.visibility = View.GONE
                atualizarResumo(txtSubtotalCarrinho, txtFreteCarrinho, txtTotalCarrinho)
            }
        }

        val bnvCarrinho = findViewById<BottomNavigationView>(R.id.bnvCarrinho)
        bnvCarrinho.selectedItemId = R.id.menuCarrinho
        bnvCarrinho.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menuProdutos -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }

                R.id.menuCarrinho -> true
                R.id.menuPerfil -> {
                    startActivity(Intent(this, PerfilActivity::class.java))
                    true
                }

                else -> false
            }
        }
    }

    private fun atualizarResumo(
        txtSubtotalCarrinho: TextView,
        txtFreteCarrinho: TextView,
        txtTotalCarrinho: TextView,
    ) {
        val subtotal = subtotalAtual.coerceAtLeast(0.0)
        val frete = if (subtotal > 0.0) freteBase else 0.0
        val total = subtotal + frete

        txtSubtotalCarrinho.text = getString(R.string.subtotal_carrinho_formatado, formatarMoeda(subtotal))
        txtFreteCarrinho.text = getString(R.string.frete_carrinho_formatado, formatarMoeda(frete))
        txtTotalCarrinho.text = getString(R.string.total_carrinho_formatado, formatarMoeda(total))
    }

    private fun formatarMoeda(valor: Double): String {
        return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(valor)
    }
}
