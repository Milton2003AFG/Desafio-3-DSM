package com.example.desafio3dsm.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.desafio3dsm.R
import com.example.desafio3dsm.controller.AuthController
import com.example.desafio3dsm.utils.SessionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var progressBar: ProgressBar
    private lateinit var tvGoRegister: TextView
    private lateinit var session: SessionManager
    private lateinit var controller: AuthController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        session = SessionManager(this)
        controller = AuthController(this)

        // Si ya hay sesión activa, redirigir directamente
        if (session.isLogueado()) {
            redirigirSegunRol(session.getRol())
            return
        }

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        progressBar = findViewById(R.id.progressBar)
        tvGoRegister = findViewById(R.id.tvGoRegister)

        btnLogin.setOnClickListener { intentarLogin() }
        tvGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun intentarLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_campos_vacios), Toast.LENGTH_SHORT).show()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, getString(R.string.error_email_invalido), Toast.LENGTH_SHORT).show()
            return
        }

        mostrarCargando(true)

        lifecycleScope.launch {
            val resultado = controller.login(email, password)
            mostrarCargando(false)

            resultado.onSuccess { usuario ->
                Toast.makeText(
                    this@LoginActivity,
                    "¡Bienvenido, ${usuario.nombre}!",
                    Toast.LENGTH_SHORT
                ).show()
                redirigirSegunRol(usuario.rol)
            }.onFailure { error ->
                Toast.makeText(this@LoginActivity, error.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun redirigirSegunRol(rol: String) {
        val intent = if (rol == "docente") {
            Intent(this, DocenteActivity::class.java)
        } else {
            Intent(this, MainActivity::class.java)
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun mostrarCargando(cargando: Boolean) {
        progressBar.visibility = if (cargando) View.VISIBLE else View.GONE
        btnLogin.isEnabled = !cargando
    }
}