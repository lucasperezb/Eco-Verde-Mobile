package com.example.projetomobile

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import java.util.Locale

class HomeActivity : AppCompatActivity() {
    private lateinit var productDb: ProductDatabaseHelper
    private lateinit var llLista: LinearLayout
    private lateinit var inflater: LayoutInflater
    private var allProducts: List<Product> = emptyList()
    private var visibleCount = 0
    private val pageSize = 5

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

        carregarProdutos(primeiraPagina = true)

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
    }

    private fun carregarProdutos(primeiraPagina: Boolean) {
        allProducts = productDb.getAllProducts()
        if (primeiraPagina) {
            visibleCount = minOf(pageSize, allProducts.size)
        }

        llLista.removeAllViews()

        allProducts.take(visibleCount).forEach { produto ->
            val item = inflater.inflate(R.layout.product_item, llLista, false)
            val nome = item.findViewById<TextView>(R.id.txtNomeProdutoItem)
            val preco = item.findViewById<TextView>(R.id.txtPrecoProdutoItem)
            val btnAdd = item.findViewById<MaterialButton>(R.id.btnAdicionarProdutoItem)
            nome.text = produto.nome
            preco.text = String.format(Locale.getDefault(), "R$ %.2f", produto.preco)
            btnAdd.setOnClickListener {
                startActivity(Intent(this, CarrinhoActivity::class.java))
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
}

