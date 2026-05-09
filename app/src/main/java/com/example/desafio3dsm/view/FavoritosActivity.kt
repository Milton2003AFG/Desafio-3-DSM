package com.example.desafio3dsm.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
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
import kotlinx.coroutines.launch

class FavoritosActivity : AppCompatActivity() {

    private lateinit var recyclerFavoritos: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutVacio: LinearLayout
    private lateinit var adapter: RecursoAdapter
    private lateinit var favoritoController: FavoritoController
    private val recursoController = RecursoController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favoritos)

        favoritoController = FavoritoController(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerFavoritos = findViewById(R.id.recyclerFavoritos)
        progressBar = findViewById(R.id.progressBar)
        layoutVacio = findViewById(R.id.layoutVacio)

        adapter = RecursoAdapter(
            lista = emptyList(),
            favoritos = emptySet(),
            onFavoritoClick = { recurso ->
                lifecycleScope.launch {
                    favoritoController.toggleFavorito(recurso.id)
                    cargarFavoritos() // Recargar al quitar favorito
                }
            },
            onRatingClick = { recurso, rating ->
                lifecycleScope.launch {
                    favoritoController.calificarRecurso(recurso.id, rating)
                    Toast.makeText(
                        this@FavoritosActivity,
                        "¡Calificación guardada!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            onItemClick = { recurso ->
                val intent = Intent(this, DetalleRecursoActivity::class.java)
                intent.putExtra("recurso_id", recurso.id)
                startActivity(intent)
            }
        )

        recyclerFavoritos.layoutManager = LinearLayoutManager(this)
        recyclerFavoritos.adapter = adapter

        cargarFavoritos()
    }

    private fun cargarFavoritos() {
        mostrarCargando(true)
        lifecycleScope.launch {
            val resultFavs = favoritoController.obtenerFavoritos()
            val resultRecursos = recursoController.obtenerRecursos()

            mostrarCargando(false)

            resultFavs.onSuccess { favIds ->
                resultRecursos.onSuccess { todosRecursos ->
                    // Filtrar solo los recursos favoritos
                    val soloFavoritos = todosRecursos.filter { r ->
                        favIds.contains(r.id)
                    }

                    if (soloFavoritos.isEmpty()) {
                        layoutVacio.visibility = View.VISIBLE
                        recyclerFavoritos.visibility = View.GONE
                    } else {
                        layoutVacio.visibility = View.GONE
                        recyclerFavoritos.visibility = View.VISIBLE
                        adapter.actualizarLista(soloFavoritos)
                        adapter.actualizarFavoritos(favIds)
                    }
                }
            }.onFailure {
                Toast.makeText(this@FavoritosActivity, it.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onResume() {
        super.onResume()
        cargarFavoritos()
    }

    private fun mostrarCargando(cargando: Boolean) {
        progressBar.visibility = if (cargando) View.VISIBLE else View.GONE
    }
}