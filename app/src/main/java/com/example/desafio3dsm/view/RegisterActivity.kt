package com.example.desafio3dsm.view

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.desafio3dsm.R
import com.example.desafio3dsm.controller.AuthController
import com.example.desafio3dsm.utils.PasswordValidator
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var etNombre: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var rgRol: RadioGroup
    private lateinit var btnRegister: MaterialButton
    private lateinit var progressBar: ProgressBar
    private lateinit var tvGoLogin: TextView
    private lateinit var controller: AuthController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        controller = AuthController(this)

        etNombre = findViewById(R.id.etNombre)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        rgRol = findViewById(R.id.rgRol)
        btnRegister = findViewById(R.id.btnRegister)
        progressBar = findViewById(R.id.progressBar)
        tvGoLogin = findViewById(R.id.tvGoLogin)

        btnRegister.setOnClickListener { intentarRegistro() }
        tvGoLogin.setOnClickListener { finish() }
    }

    private fun intentarRegistro() {
        val nombre = etNombre.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val rol = if (rgRol.checkedRadioButtonId == R.id.rbDocente) "docente" else "estudiante"

        // Validaciones
        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_campos_vacios), Toast.LENGTH_SHORT).show()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, getString(R.string.error_email_invalido), Toast.LENGTH_SHORT).show()
            return
        }

        val errorPassword = PasswordValidator.validar(password)
        if (errorPassword != null) {
            Toast.makeText(this, errorPassword, Toast.LENGTH_LONG).show()
            return
        }

        mostrarCargando(true)

        lifecycleScope.launch {
            val resultado = controller.registrar(nombre, email, password, rol)
            mostrarCargando(false)

            resultado.onSuccess {
                Toast.makeText(
                    this@RegisterActivity,
                    getString(R.string.success_register),
                    Toast.LENGTH_SHORT
                ).show()
                finish() // Volver al Login
            }.onFailure { error ->
                Toast.makeText(this@RegisterActivity, error.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun mostrarCargando(cargando: Boolean) {
        progressBar.visibility = if (cargando) View.VISIBLE else View.GONE
        btnRegister.isEnabled = !cargando
    }
}