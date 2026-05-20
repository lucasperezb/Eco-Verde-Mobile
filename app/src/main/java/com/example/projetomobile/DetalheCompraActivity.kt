package com.example.projetomobile

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONArray
import java.text.NumberFormat
import java.util.Locale

class DetalheCompraActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detalhe_compra)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainDetalheCompra)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val purchaseId = intent.getLongExtra("PURCHASE_ID", -1L)
        if (purchaseId == -1L) {
            Toast.makeText(this, "Compra não encontrada", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val purchaseDb = PurchaseDatabaseHelper(this)
        val purchase = purchaseDb.getPurchase(purchaseId)

        if (purchase == null) {
            Toast.makeText(this, "Compra não encontrada", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Preencher informações da compra
        findViewById<TextView>(R.id.txtProtocolo).text = getString(R.string.label_protocolo, purchase.protocolo)
        findViewById<TextView>(R.id.txtDataCompra).text = getString(R.string.label_data_compra, purchase.dataCompra)
        findViewById<TextView>(R.id.txtStatusCompra).text = getString(R.string.label_status_compra, purchase.status)

        // Preencher produtos
        val llProdutos = findViewById<LinearLayout>(R.id.llProdutosCompra)
        try {
            val produtosArray = JSONArray(purchase.produtos)
            for (i in 0 until produtosArray.length()) {
                val obj = produtosArray.getJSONObject(i)
                val nome = obj.getString("nome")
                val qtd = obj.getInt("quantidade")
                val preco = obj.getDouble("preco")

                val tvProduto = TextView(this).apply {
                    text = String.format(Locale.getDefault(), "%s x %d = R$ %.2f", nome, qtd, preco * qtd)
                    textSize = 14f
                    setTextColor(ContextCompat.getColor(this@DetalheCompraActivity, android.R.color.black))
                    setPadding(0, 8, 0, 0)
                }
                llProdutos.addView(tvProduto)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Erro ao carregar produtos", Toast.LENGTH_SHORT).show()
        }

        // Preencher resumo
        findViewById<TextView>(R.id.txtSubtotalDetalhe).text =
            "Subtotal: ${formatarMoeda(purchase.subtotal)}"
        findViewById<TextView>(R.id.txtFreteDetalhe).text =
            "Frete: ${formatarMoeda(purchase.frete)}"
        findViewById<TextView>(R.id.txtTotalDetalhe).text =
            "Total: ${formatarMoeda(purchase.total)}"
    }

    private fun formatarMoeda(valor: Double): String {
        return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(valor)
    }
}



