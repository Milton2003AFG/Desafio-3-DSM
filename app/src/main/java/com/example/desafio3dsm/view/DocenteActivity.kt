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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.desafio3dsm.R
import com.example.desafio3dsm.controller.RecursoController
import com.example.desafio3dsm.model.Recurso
import com.example.desafio3dsm.utils.SessionManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class DocenteActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var etBuscar: TextInputEditText
    private lateinit var fabAgregar: FloatingActionButton
    private lateinit var adapter: RecursoDocenteAdapter
    private lateinit var session: SessionManager
    private val controller = RecursoController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_docente)

        session = SessionManager(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Panel Docente · ${session.getNombre()}"

        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        etBuscar = findViewById(R.id.etBuscar)
        fabAgregar = findViewById(R.id.fabAgregar)

        adapter = RecursoDocenteAdapter(
            lista = emptyList(),
            onEditar = { recurso ->
                val intent = Intent(this, AgregarEditarActivity::class.java)
                intent.putExtra("recurso_id", recurso.id)
                intent.putExtra("titulo", recurso.titulo)
                intent.putExtra("descripcion", recurso.descripcion)
                intent.putExtra("tipo", recurso.tipo)
                intent.putExtra("enlace", recurso.enlace)
                intent.putExtra("imagen", recurso.imagen)
                startActivity(intent)
            },
            onEliminar = { recurso ->
                confirmarEliminar(recurso)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fabAgregar.setOnClickListener {
            startActivity(Intent(this, AgregarEditarActivity::class.java))
        }

        // Búsqueda en tiempo real
        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                lifecycleScope.launch {
                    if (query.isEmpty()) cargarRecursos()
                    else {
                        val resultado = controller.buscarRecursos(query)
                        resultado.onSuccess { adapter.actualizarLista(it) }
                    }
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        cargarRecursos()
    }

    private fun confirmarEliminar(recurso: Recurso) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar recurso")
            .setMessage("¿Estás seguro que deseas eliminar \"${recurso.titulo}\"?")
            .setPositiveButton("Eliminar") { _, _ ->
                lifecycleScope.launch {
                    mostrarCargando(true)
                    val resultado = controller.eliminarRecurso(recurso.id)
                    mostrarCargando(false)
                    resultado.onSuccess {
                        Toast.makeText(
                            this@DocenteActivity,
                            "Recurso eliminado",
                            Toast.LENGTH_SHORT
                        ).show()
                        cargarRecursos()
                    }.onFailure {
                        Toast.makeText(this@DocenteActivity, it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun cargarRecursos() {
        mostrarCargando(true)
        lifecycleScope.launch {
            val resultado = controller.obtenerRecursos()
            mostrarCargando(false)
            resultado.onSuccess { adapter.actualizarLista(it) }
                .onFailure {
                    Toast.makeText(this@DocenteActivity, it.message, Toast.LENGTH_LONG).show()
                }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_docente, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
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