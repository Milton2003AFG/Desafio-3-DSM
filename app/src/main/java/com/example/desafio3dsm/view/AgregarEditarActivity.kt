package com.example.desafio3dsm.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.desafio3dsm.R
import com.example.desafio3dsm.controller.RecursoController
import com.example.desafio3dsm.model.Recurso
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class AgregarEditarActivity : AppCompatActivity() {

    private lateinit var tvFormTitulo: TextView
    private lateinit var etTitulo: TextInputEditText
    private lateinit var etDescripcion: TextInputEditText
    private lateinit var acTipo: AutoCompleteTextView
    private lateinit var etEnlace: TextInputEditText
    private lateinit var etImagen: TextInputEditText
    private lateinit var ivPreview: ImageView
    private lateinit var btnGuardar: MaterialButton
    private lateinit var progressBar: ProgressBar

    private val controller = RecursoController()
    private var recursoId: String? = null
    private val tipos = listOf("libro", "video", "articulo", "tutorial")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_editar)

        tvFormTitulo = findViewById(R.id.tvFormTitulo)
        etTitulo = findViewById(R.id.etTitulo)
        etDescripcion = findViewById(R.id.etDescripcion)
        acTipo = findViewById(R.id.acTipo)
        etEnlace = findViewById(R.id.etEnlace)
        etImagen = findViewById(R.id.etImagen)
        ivPreview = findViewById(R.id.ivPreview)
        btnGuardar = findViewById(R.id.btnGuardar)
        progressBar = findViewById(R.id.progressBar)

        // Dropdown de tipos
        val tipoAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, tipos)
        acTipo.setAdapter(tipoAdapter)

        // Verificar si es edición
        recursoId = intent.getStringExtra("recurso_id")
        if (recursoId != null) {
            tvFormTitulo.text = "Editar Recurso"
            etTitulo.setText(intent.getStringExtra("titulo"))
            etDescripcion.setText(intent.getStringExtra("descripcion"))
            acTipo.setText(intent.getStringExtra("tipo"), false)
            etEnlace.setText(intent.getStringExtra("enlace"))
            etImagen.setText(intent.getStringExtra("imagen"))
            cargarPreviewImagen(intent.getStringExtra("imagen") ?: "")
        }

        // Preview de imagen en tiempo real
        etImagen.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val url = s.toString().trim()
                if (url.isNotEmpty()) cargarPreviewImagen(url)
                else ivPreview.visibility = View.GONE
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        btnGuardar.setOnClickListener { guardarRecurso() }
    }

    private fun cargarPreviewImagen(url: String) {
        ivPreview.visibility = View.VISIBLE
        Glide.with(this).load(url)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_gallery)
            .into(ivPreview)
    }

    private fun guardarRecurso() {
        val titulo = etTitulo.text.toString().trim()
        val descripcion = etDescripcion.text.toString().trim()
        val tipo = acTipo.text.toString().trim()
        val enlace = etEnlace.text.toString().trim()
        val imagen = etImagen.text.toString().trim()

        // Validaciones
        if (titulo.isEmpty() || descripcion.isEmpty() || tipo.isEmpty() || enlace.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (!tipos.contains(tipo.lowercase())) {
            Toast.makeText(this, "Selecciona un tipo válido de la lista", Toast.LENGTH_SHORT).show()
            return
        }

        mostrarCargando(true)

        lifecycleScope.launch {
            val recurso = Recurso(
                titulo = titulo,
                descripcion = descripcion,
                tipo = tipo.lowercase(),
                enlace = enlace,
                imagen = imagen
            )

            val resultado = if (recursoId != null) {
                controller.actualizarRecurso(recursoId!!, recurso)
            } else {
                controller.crearRecurso(recurso)
            }

            mostrarCargando(false)

            resultado.onSuccess {
                val msg = if (recursoId != null) "Recurso actualizado" else "Recurso creado"
                Toast.makeText(this@AgregarEditarActivity, msg, Toast.LENGTH_SHORT).show()
                finish()
            }.onFailure {
                Toast.makeText(this@AgregarEditarActivity, it.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun mostrarCargando(cargando: Boolean) {
        progressBar.visibility = if (cargando) View.VISIBLE else View.GONE
        btnGuardar.isEnabled = !cargando
    }
}