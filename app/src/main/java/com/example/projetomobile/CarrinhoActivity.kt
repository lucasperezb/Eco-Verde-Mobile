package com.example.projetomobile

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import org.json.JSONArray
import org.json.JSONObject
import java.text.NumberFormat
import java.util.Locale

class CarrinhoActivity : AppCompatActivity() {
    private val freteBase = 12.00
    private var subtotalAtual = 0.0
    private var carrinhoItems = JSONArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_carrinho)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainCarrinho)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val txtSubtotalCarrinho = findViewById<TextView>(R.id.txtSubtotalCarrinho)
        val txtFreteCarrinho = findViewById<TextView>(R.id.txtFreteCarrinho)
        val txtTotalCarrinho = findViewById<TextView>(R.id.txtTotalCarrinho)
        val btnFinalizarCompra = findViewById<MaterialButton>(R.id.btnFinalizarCompra)
        val llProdutosCarrinho = findViewById<LinearLayout>(R.id.llListaProdutosCarrinho)

        carregarCarrinho(llProdutosCarrinho, txtSubtotalCarrinho, txtFreteCarrinho, txtTotalCarrinho)

        btnFinalizarCompra.setOnClickListener {
            finalizarCompra()
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

    override fun onResume() {
        super.onResume()
        val txtSubtotalCarrinho = findViewById<TextView>(R.id.txtSubtotalCarrinho)
        val txtFreteCarrinho = findViewById<TextView>(R.id.txtFreteCarrinho)
        val txtTotalCarrinho = findViewById<TextView>(R.id.txtTotalCarrinho)
        val llProdutosCarrinho = findViewById<LinearLayout>(R.id.llListaProdutosCarrinho)
        carregarCarrinho(llProdutosCarrinho, txtSubtotalCarrinho, txtFreteCarrinho, txtTotalCarrinho)
    }

    private fun carregarCarrinho(llProdutos: LinearLayout, txtSubtotal: TextView, txtFrete: TextView, txtTotal: TextView) {
        val sharedPref = getSharedPreferences("eco_verde_cart", MODE_PRIVATE)
        val carrinhoJson = sharedPref.getString("carrinho_items", "[]") ?: "[]"
        
        llProdutos.removeAllViews()
        subtotalAtual = 0.0

        try {
            carrinhoItems = JSONArray(carrinhoJson)
            
            if (carrinhoItems.length() == 0) {
                val emptyText = TextView(this).apply {
                    text = "Seu carrinho está vazio"
                    textSize = 14f
                    setPadding(16, 16, 16, 16)
                }
                llProdutos.addView(emptyText)
                atualizarResumo(txtSubtotal, txtFrete, txtTotal)
                return
            }

            for (i in 0 until carrinhoItems.length()) {
                val obj = carrinhoItems.getJSONObject(i)
                val nome = obj.getString("nome")
                val preco = obj.getDouble("preco")
                val qtd = obj.getInt("quantidade")
                val id = obj.getLong("id")
                val total = preco * qtd
                subtotalAtual += total

                val tvProduto = TextView(this).apply {
                    text = "$nome x $qtd = R$ ${String.format("%.2f", total)}"
                    textSize = 14f
                    setPadding(8, 8, 8, 8)
                }
                llProdutos.addView(tvProduto)

                val btnRemover = MaterialButton(this).apply {
                    text = "Remover"
                    setAllCaps(false)
                    setOnClickListener {
                        removerDoCarrinho(i)
                    }
                }
                llProdutos.addView(btnRemover)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Erro ao carregar carrinho", Toast.LENGTH_SHORT).show()
        }

        atualizarResumo(txtSubtotal, txtFrete, txtTotal)
    }

    private fun removerDoCarrinho(index: Int) {
        val sharedPref = getSharedPreferences("eco_verde_cart", MODE_PRIVATE)
        
        if (index >= 0 && index < carrinhoItems.length()) {
            carrinhoItems.remove(index)
        }

        with(sharedPref.edit()) {
            putString("carrinho_items", carrinhoItems.toString())
            apply()
        }

        val txtSubtotalCarrinho = findViewById<TextView>(R.id.txtSubtotalCarrinho)
        val txtFreteCarrinho = findViewById<TextView>(R.id.txtFreteCarrinho)
        val txtTotalCarrinho = findViewById<TextView>(R.id.txtTotalCarrinho)
        val llProdutosCarrinho = findViewById<LinearLayout>(R.id.llListaProdutosCarrinho)
        
        carregarCarrinho(llProdutosCarrinho, txtSubtotalCarrinho, txtFreteCarrinho, txtTotalCarrinho)
        Toast.makeText(this, "Produto removido", Toast.LENGTH_SHORT).show()
    }

    private fun finalizarCompra() {
        if (subtotalAtual <= 0.0) {
            Toast.makeText(this, "Carrinho vazio. Adicione produtos antes de finalizar.", Toast.LENGTH_LONG).show()
            return
        }

        val sharedPref = getSharedPreferences("eco_verde_prefs", MODE_PRIVATE)
        val userId = sharedPref.getLong("user_id", -1L)

        if (userId == -1L) {
            Toast.makeText(this, "Usuário não encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        val protocolo = generateProtocolo()
        val dataCompra = getCurrentDate()

        val frete = if (subtotalAtual > 0.0) freteBase else 0.0
        val total = subtotalAtual + frete

        val purchase = Purchase(
            userId = userId,
            protocolo = protocolo,
            dataCompra = dataCompra,
            subtotal = subtotalAtual,
            frete = frete,
            total = total,
            produtos = carrinhoItems.toString(),
            status = "confirmada"
        )

        val purchaseDb = PurchaseDatabaseHelper(this)
        val id = purchaseDb.insertPurchase(purchase)
        
        if (id != -1L) {
            Toast.makeText(this, getString(R.string.msg_compra_realizada, protocolo), Toast.LENGTH_LONG).show()
            
            // Limpar carrinho
            with(getSharedPreferences("eco_verde_cart", MODE_PRIVATE).edit()) {
                putString("carrinho_items", "[]")
                apply()
            }
            
            subtotalAtual = 0.0
            val txtSubtotalCarrinho = findViewById<TextView>(R.id.txtSubtotalCarrinho)
            val txtFreteCarrinho = findViewById<TextView>(R.id.txtFreteCarrinho)
            val txtTotalCarrinho = findViewById<TextView>(R.id.txtTotalCarrinho)
            val llProdutosCarrinho = findViewById<LinearLayout>(R.id.llListaProdutosCarrinho)
            
            carregarCarrinho(llProdutosCarrinho, txtSubtotalCarrinho, txtFreteCarrinho, txtTotalCarrinho)
        } else {
            Toast.makeText(this, "Erro ao salvar compra", Toast.LENGTH_SHORT).show()
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

    private fun generateProtocolo(): String {
        val timestamp = System.currentTimeMillis().toString().takeLast(8)
        val random = kotlin.random.Random.nextInt(10000, 99999)
        return "PRO-$timestamp-$random"
    }

    private fun getCurrentDate(): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        return sdf.format(java.util.Date())
    }
}
