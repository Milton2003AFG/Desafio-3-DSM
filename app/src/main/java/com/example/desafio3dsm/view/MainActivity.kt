package com.example.desafio3dsm.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.desafio3dsm.R
import com.example.desafio3dsm.controller.FavoritoController
import com.example.desafio3dsm.controller.RecursoController
import com.example.desafio3dsm.utils.SessionManager
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var etBuscar: TextInputEditText
    private lateinit var chipGroup: ChipGroup
    private lateinit var adapter: RecursoAdapter
    private lateinit var session: SessionManager
    private val recursoController = RecursoController()
    private lateinit var favoritoController: FavoritoController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        session = SessionManager(this)
        favoritoController = FavoritoController(this)

        // Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Hola, ${session.getNombre()}"

        // Views
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        etBuscar = findViewById(R.id.etBuscar)
        chipGroup = findViewById(R.id.chipGroupFiltros)

        // Adapter
        adapter = RecursoAdapter(
            lista = emptyList(),
            favoritos = emptySet(),
            onFavoritoClick = { recurso ->
                lifecycleScope.launch {
                    val resultado = favoritoController.toggleFavorito(recurso.id)
                    resultado.onSuccess { nuevosFavs ->
                        adapter.actualizarFavoritos(nuevosFavs)
                        Toast.makeText(this@MainActivity, "Favoritos actualizados", Toast.LENGTH_SHORT).show()
                    }.onFailure {
                        Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onRatingClick = { recurso, rating ->
                lifecycleScope.launch {
                    val resultado = favoritoController.calificarRecurso(recurso.id, rating)
                    resultado.onSuccess {
                        Toast.makeText(this@MainActivity, "¡Calificación guardada!", Toast.LENGTH_SHORT).show()
                        cargarRecursos()
                    }.onFailure {
                        Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onItemClick = { recurso ->
                val intent = Intent(this, DetalleRecursoActivity::class.java)
                intent.putExtra("recurso_id", recurso.id)
                startActivity(intent)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Búsqueda en tiempo real
        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                lifecycleScope.launch {
                    if (query.isEmpty()) cargarRecursos()
                    else {
                        val resultado = recursoController.buscarRecursos(query)
                        resultado.onSuccess { adapter.actualizarLista(it) }
                    }
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Filtros por chip
        chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            val tipo = when {
                checkedIds.contains(R.id.chipLibro) -> "libro"
                checkedIds.contains(R.id.chipVideo) -> "video"
                checkedIds.contains(R.id.chipArticulo) -> "articulo"
                checkedIds.contains(R.id.chipTutorial) -> "tutorial"
                else -> "Todos"
            }
            lifecycleScope.launch {
                val resultado = recursoController.filtrarPorTipo(tipo)
                resultado.onSuccess { adapter.actualizarLista(it) }
            }
        }

        cargarRecursos()
    }

    private fun cargarRecursos() {
        mostrarCargando(true)
        lifecycleScope.launch {
            // Cargar recursos y favoritos en paralelo
            val resultRecursos = recursoController.obtenerRecursos()
            val resultFavs = favoritoController.obtenerFavoritos()

            mostrarCargando(false)

            resultRecursos.onSuccess { recursos ->
                val favs = resultFavs.getOrDefault(emptySet())
                adapter.actualizarLista(recursos)
                adapter.actualizarFavoritos(favs)
            }.onFailure {
                Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    // Menú con opción de Logout y Favoritos
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_favoritos -> {
                startActivity(Intent(this, FavoritosActivity::class.java))
                true
            }
            R.id.action_logout -> {
                session.cerrarSesion()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        cargarRecursos()
    }

    private fun mostrarCargando(cargando: Boolean) {
        progressBar.visibility = if (cargando) View.VISIBLE else View.GONE
    }
}