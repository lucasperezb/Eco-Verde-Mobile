package com.example.projetomobile

import androidx.activity.OnBackPressedCallback
import android.os.Bundle
import android.text.InputType
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Tela simples de administração para gestão de estoque (adicionar/editar/excluir produtos).
 * Implementação mínima: usa dialogs para operação sem criar layouts XML adicionais.
 */
class AdminActivity : AppCompatActivity() {
    private lateinit var productDb: ProductDatabaseHelper
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Usar layout dedicado para admin
        setContentView(R.layout.activity_admin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tvAdminTitle)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        productDb = ProductDatabaseHelper(this)

        val btnMenu = findViewById<Button>(R.id.btnAdminMenu)
        btnMenu.setOnClickListener { showAdminMenu() }

        listView = findViewById(R.id.lstAdminProducts)
        refreshProductList()

        // Intercept back presses to show admin menu instead of closing
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showAdminMenu()
            }
        })
    }

    private fun showAdminMenu() {
        val options = arrayOf("Listar produtos", "Adicionar produto", "Editar estoque", "Deletar produto", "Sair")
        AlertDialog.Builder(this)
            .setTitle("Painel Admin")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> listProducts()
                    1 -> addProductDialog()
                    2 -> editStockDialog()
                    3 -> deleteProductDialog()
                    4 -> finish()
                }
            }
            .setCancelable(false)
            .show()
    }

    private fun listProducts() {
        // Atualiza a lista visível
        refreshProductList()
    }

    private fun refreshProductList() {
        val produtos = productDb.getAllProducts()
        if (produtos.isEmpty()) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listOf(getString(R.string.nenhum_produto_cadastrado)))
            listView.adapter = adapter
            listView.setOnItemClickListener(null)
            return
        }

        val items = produtos.map { "${it.id}: ${it.nome} (estoque: ${it.estoque}) - R$ ${it.preco}" }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            val prod = produtos[position]
            AlertDialog.Builder(this)
                .setTitle(prod.nome)
                .setMessage("ID: ${prod.id}\nPreço: R$ ${prod.preco}\nEstoque: ${prod.estoque}\nCategoria: ${prod.categoria}\n\nDescrição:\n${prod.descricao}")
                .setPositiveButton("Ok") { _, _ -> }
                .show()
        }
    }

    private fun addProductDialog() {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        val nomeInput = EditText(this)
        nomeInput.hint = "Nome"
        val descInput = EditText(this)
        descInput.hint = "Descrição"
        val precoInput = EditText(this)
        precoInput.hint = "Preço"
        precoInput.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        val categoriaInput = EditText(this)
        categoriaInput.hint = "Categoria"
        val estoqueInput = EditText(this)
        estoqueInput.hint = "Estoque"
        estoqueInput.inputType = InputType.TYPE_CLASS_NUMBER

        layout.addView(nomeInput)
        layout.addView(descInput)
        layout.addView(precoInput)
        layout.addView(categoriaInput)
        layout.addView(estoqueInput)

        AlertDialog.Builder(this)
            .setTitle("Adicionar Produto")
            .setView(layout)
            .setPositiveButton("Salvar") { _, _ ->
                val nome = nomeInput.text.toString().trim()
                val descricao = descInput.text.toString().trim()
                val preco = precoInput.text.toString().toDoubleOrNull() ?: 0.0
                val categoria = categoriaInput.text.toString().trim().ifEmpty { "Geral" }
                val estoque = estoqueInput.text.toString().toIntOrNull() ?: 0

                if (nome.isEmpty()) {
                    Toast.makeText(this, "Nome é obrigatório.", Toast.LENGTH_LONG).show()
                    showAdminMenu()
                    return@setPositiveButton
                }

                val novo = Product(nome = nome, descricao = descricao, preco = preco, categoria = categoria, estoque = estoque)
                val id = productDb.insertProduct(novo)
                if (id != -1L) Toast.makeText(this, "Produto adicionado.", Toast.LENGTH_LONG).show()
                else Toast.makeText(this, "Erro ao adicionar produto.", Toast.LENGTH_LONG).show()
                showAdminMenu()
            }
            .setNegativeButton("Cancelar") { _, _ -> showAdminMenu() }
            .show()
    }

    private fun editStockDialog() {
        val produtos = productDb.getAllProducts()
        if (produtos.isEmpty()) {
            Toast.makeText(this, "Nenhum produto para editar.", Toast.LENGTH_LONG).show()
            showAdminMenu()
            return
        }
        val names = produtos.map { "${it.id}: ${it.nome} (estoque: ${it.estoque})" }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Escolha um produto")
            .setItems(names) { _, which ->
                val prod = produtos[which]
                val input = EditText(this)
                input.hint = "Novo estoque"
                input.inputType = InputType.TYPE_CLASS_NUMBER
                input.setText(prod.estoque.toString())
                AlertDialog.Builder(this)
                    .setTitle("Editar estoque - ${prod.nome}")
                    .setView(input)
                    .setPositiveButton("Salvar") { _, _ ->
                        val novoEstoque = input.text.toString().toIntOrNull() ?: prod.estoque
                        val atualizado = prod.copy(estoque = novoEstoque)
                        val rows = productDb.updateProduct(atualizado)
                        if (rows > 0) Toast.makeText(this, "Estoque atualizado.", Toast.LENGTH_LONG).show()
                        else Toast.makeText(this, "Erro ao atualizar estoque.", Toast.LENGTH_LONG).show()
                        showAdminMenu()
                    }
                    .setNegativeButton("Cancelar") { _, _ -> showAdminMenu() }
                    .show()
            }
            .setNegativeButton("Cancelar") { _, _ -> showAdminMenu() }
            .show()
    }

    private fun deleteProductDialog() {
        val produtos = productDb.getAllProducts()
        if (produtos.isEmpty()) {
            Toast.makeText(this, "Nenhum produto para deletar.", Toast.LENGTH_LONG).show()
            showAdminMenu()
            return
        }
        val names = produtos.map { "${it.id}: ${it.nome}" }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Escolha um produto para deletar")
            .setItems(names) { _, which ->
                val prod = produtos[which]
                AlertDialog.Builder(this)
                    .setTitle("Confirmar")
                    .setMessage("Deletar ${prod.nome} ?")
                    .setPositiveButton("Sim") { _, _ ->
                        val rows = productDb.deleteProduct(prod.id ?: -1L)
                        if (rows > 0) Toast.makeText(this, "Produto deletado.", Toast.LENGTH_LONG).show()
                        else Toast.makeText(this, "Erro ao deletar.", Toast.LENGTH_LONG).show()
                        showAdminMenu()
                    }
                    .setNegativeButton("Não") { _, _ -> showAdminMenu() }
                    .show()
            }
            .setNegativeButton("Cancelar") { _, _ -> showAdminMenu() }
            .show()
    }

    // Back press handled via OnBackPressedDispatcher callback
}




