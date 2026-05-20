package com.example.projetomobile

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Toast
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import androidx.core.content.ContextCompat
import org.json.JSONArray
import java.text.NumberFormat
import java.util.Locale

class HomeActivity : AppCompatActivity() {
    private lateinit var productDb: ProductDatabaseHelper
    private lateinit var llLista: LinearLayout
    private lateinit var inflater: LayoutInflater
    private var allProducts: List<Product> = emptyList()
    private var visibleCount = 0
    private val pageSize = 5
    private lateinit var txtItensCarrinhoHome: TextView
    private lateinit var txtValorCarrinhoHome: TextView

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

        productDb = ProductDatabaseHelper(this)
        llLista = findViewById(R.id.llListaProdutosHome)
        inflater = LayoutInflater.from(this)
        txtItensCarrinhoHome = findViewById(R.id.txtItensCarrinhoHome)
        txtValorCarrinhoHome = findViewById(R.id.txtValorCarrinhoHome)

        carregarProdutos(primeiraPagina = true)
        atualizarResumoCarrinhoHome()

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

    override fun onResume() {
        super.onResume()
        carregarProdutos(primeiraPagina = true)
        atualizarResumoCarrinhoHome()
    }

    private fun carregarProdutos(primeiraPagina: Boolean) {
        allProducts = productDb.getAllProducts()
        if (primeiraPagina) {
            visibleCount = minOf(pageSize, allProducts.size)
        }

        llLista.removeAllViews()

        if (allProducts.isEmpty()) {
            val emptyState = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams = params
                setPadding(8, 24, 8, 24)
            }

            val title = TextView(this).apply {
                text = getString(R.string.nenhum_produto_cadastrado)
                setTextColor(ContextCompat.getColor(this@HomeActivity, R.color.green_text_primary))
                textSize = 16f
                setTypeface(typeface, android.graphics.Typeface.BOLD)
            }

            val subtitle = TextView(this).apply {
                text = getString(R.string.subtitulo_nenhum_produto)
                setTextColor(ContextCompat.getColor(this@HomeActivity, R.color.green_text_secondary))
                textSize = 14f
                setPadding(0, 8, 0, 0)
            }

            emptyState.addView(title)
            emptyState.addView(subtitle)
            llLista.addView(emptyState)
            return
        }

        allProducts.take(visibleCount).forEach { produto ->
            val item = inflater.inflate(R.layout.product_item, llLista, false)
            val nome = item.findViewById<TextView>(R.id.txtNomeProdutoItem)
            val preco = item.findViewById<TextView>(R.id.txtPrecoProdutoItem)
            val btnAdd = item.findViewById<MaterialButton>(R.id.btnAdicionarProdutoItem)
            nome.text = produto.nome
            preco.text = String.format(Locale.getDefault(), "R$ %.2f", produto.preco)
            btnAdd.setOnClickListener {
                adicionarAoCarrinho(produto)
            }
            llLista.addView(item)
        }

        if (visibleCount < allProducts.size) {
            val btnCarregarMais = MaterialButton(this).apply {
                setText(R.string.carregar_mais_produtos)
                setAllCaps(false)
                setOnClickListener {
                    visibleCount = minOf(visibleCount + pageSize, allProducts.size)
                    carregarProdutos(primeiraPagina = false)
                }
            }
            llLista.addView(btnCarregarMais)
        }
    }

    private fun adicionarAoCarrinho(produto: Product) {
        val sharedPref = getSharedPreferences("eco_verde_cart", MODE_PRIVATE)
        val carrinhoJson = sharedPref.getString("carrinho_items", "[]") ?: "[]"
        
        try {
            val carrinhoArray = JSONArray(carrinhoJson)
            
            // Verifica se o produto já existe no carrinho
            var encontrado = false
            for (i in 0 until carrinhoArray.length()) {
                val obj = carrinhoArray.getJSONObject(i)
                if (obj.getLong("id") == (produto.id ?: -1L)) {
                    // Aumenta a quantidade
                    obj.put("quantidade", obj.getInt("quantidade") + 1)
                    encontrado = true
                    break
                }
            }
            
            // Se não existe, adiciona novo item
            if (!encontrado) {
                val novoItem = org.json.JSONObject().apply {
                    put("id", produto.id)
                    put("nome", produto.nome)
                    put("preco", produto.preco)
                    put("quantidade", 1)
                }
                carrinhoArray.put(novoItem)
            }
            
            // Salva no SharedPreferences
            with(sharedPref.edit()) {
                putString("carrinho_items", carrinhoArray.toString())
                apply()
            }

            atualizarResumoCarrinhoHome()
            
            Toast.makeText(this, "${produto.nome} adicionado ao carrinho!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Erro ao adicionar ao carrinho", Toast.LENGTH_SHORT).show()
        }
    }

    private fun atualizarResumoCarrinhoHome() {
        val sharedPref = getSharedPreferences("eco_verde_cart", MODE_PRIVATE)
        val carrinhoJson = sharedPref.getString("carrinho_items", "[]") ?: "[]"

        try {
            val carrinhoArray = JSONArray(carrinhoJson)
            var quantidadeItens = 0
            var valorTotal = 0.0

            for (i in 0 until carrinhoArray.length()) {
                val item = carrinhoArray.getJSONObject(i)
                val quantidade = item.optInt("quantidade", 0)
                val preco = item.optDouble("preco", 0.0)
                quantidadeItens += quantidade
                valorTotal += preco * quantidade
            }

            txtItensCarrinhoHome.text = getString(R.string.itens_carrinho_home_formatado, quantidadeItens)
            txtValorCarrinhoHome.text = getString(R.string.valor_carrinho_home_formatado, formatarMoeda(valorTotal))
        } catch (_: Exception) {
            txtItensCarrinhoHome.text = getString(R.string.itens_carrinho_home)
            txtValorCarrinhoHome.text = getString(R.string.valor_carrinho_home)
        }
    }

    private fun formatarMoeda(valor: Double): String {
        return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(valor)
    }
}

