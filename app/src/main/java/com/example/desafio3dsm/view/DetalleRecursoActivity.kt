package com.example.desafio3dsm.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.desafio3dsm.R
import com.example.desafio3dsm.controller.FavoritoController
import com.example.desafio3dsm.controller.RecursoController
import com.example.desafio3dsm.model.Recurso
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch

class DetalleRecursoActivity : AppCompatActivity() {

    private lateinit var ivImagen: ImageView
    private lateinit var chipTipo: Chip
    private lateinit var tvTitulo: TextView
    private lateinit var tvDescripcion: TextView
    private lateinit var ratingBarDetalle: RatingBar
    private lateinit var tvRatingDetalle: TextView
    private lateinit var ratingBarUsuario: RatingBar
    private lateinit var btnAbrirEnlace: MaterialButton
    private lateinit var btnFavorito: MaterialButton
    private lateinit var progressBar: ProgressBar

    private val recursoController = RecursoController()
    private lateinit var favoritoController: FavoritoController
    private var recursoActual: Recurso? = null
    private var esFavorito = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_recurso)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detalle del Recurso"

        favoritoController = FavoritoController(this)

        ivImagen = findViewById(R.id.ivImagen)
        chipTipo = findViewById(R.id.chipTipo)
        tvTitulo = findViewById(R.id.tvTitulo)
        tvDescripcion = findViewById(R.id.tvDescripcion)
        ratingBarDetalle = findViewById(R.id.ratingBarDetalle)
        tvRatingDetalle = findViewById(R.id.tvRatingDetalle)
        ratingBarUsuario = findViewById(R.id.ratingBarUsuario)
        btnAbrirEnlace = findViewById(R.id.btnAbrirEnlace)
        btnFavorito = findViewById(R.id.btnFavorito)
        progressBar = findViewById(R.id.progressBar)

        val recursoId = intent.getStringExtra("recurso_id") ?: run {
            Toast.makeText(this, "Recurso no encontrado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        cargarDetalle(recursoId)

        // Calificar recurso
        ratingBarUsuario.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser) {
                lifecycleScope.launch {
                    val resultado = favoritoController.calificarRecurso(recursoId, rating)
                    resultado.onSuccess {
                        Toast.makeText(
                            this@DetalleRecursoActivity,
                            "¡Calificación guardada!",
                            Toast.LENGTH_SHORT
                        ).show()
                        cargarDetalle(recursoId)
                    }.onFailure {
                        Toast.makeText(this@DetalleRecursoActivity, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Abrir enlace en navegador
        btnAbrirEnlace.setOnClickListener {
            val enlace = recursoActual?.enlace ?: return@setOnClickListener
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(enlace))
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "No se pudo abrir el enlace", Toast.LENGTH_SHORT).show()
            }
        }

        // Toggle favorito
        btnFavorito.setOnClickListener {
            lifecycleScope.launch {
                val resultado = favoritoController.toggleFavorito(recursoId)
                resultado.onSuccess { nuevosFavs ->
                    esFavorito = nuevosFavs.contains(recursoId)
                    actualizarBotonFavorito()
                    val msg = if (esFavorito) "Agregado a favoritos" else "Eliminado de favoritos"
                    Toast.makeText(this@DetalleRecursoActivity, msg, Toast.LENGTH_SHORT).show()
                }.onFailure {
                    Toast.makeText(this@DetalleRecursoActivity, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun cargarDetalle(recursoId: String) {
        mostrarCargando(true)
        lifecycleScope.launch {
            try {
                val recurso = recursoController.obtenerRecursoPorId(recursoId)
                val favs = favoritoController.obtenerFavoritos()

                mostrarCargando(false)

                recurso.onSuccess { r ->
                    recursoActual = r
                    poblarUI(r)

                    favs.onSuccess { set ->
                        esFavorito = set.contains(recursoId)
                        actualizarBotonFavorito()
                    }
                }.onFailure {
                    Toast.makeText(
                        this@DetalleRecursoActivity,
                        it.message,
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            } catch (e: Exception) {
                mostrarCargando(false)
                Toast.makeText(this@DetalleRecursoActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun poblarUI(recurso: Recurso) {
        tvTitulo.text = recurso.titulo
        tvDescripcion.text = recurso.descripcion
        chipTipo.text = recurso.tipo
        ratingBarDetalle.rating = recurso.rating
        tvRatingDetalle.text = "%.1f★ · %d votos".format(recurso.rating, recurso.totalRating)

        Glide.with(this)
            .load(recurso.imagen)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_gallery)
            .into(ivImagen)
    }

    private fun actualizarBotonFavorito() {
        if (esFavorito) {
            btnFavorito.text = "Quitar de Favoritos"
            btnFavorito.setIconResource(android.R.drawable.btn_star_big_on)
        } else {
            btnFavorito.text = "Guardar en Favoritos"
            btnFavorito.setIconResource(android.R.drawable.btn_star_big_off)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun mostrarCargando(cargando: Boolean) {
        progressBar.visibility = if (cargando) View.VISIBLE else View.GONE
    }
}