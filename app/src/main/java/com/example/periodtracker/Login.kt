package com.example.periodtracker

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        // Inicializar FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Verificar si hay una sesión activa
        val usuarioActual = auth.currentUser
        if (usuarioActual != null) {
            // Redirigir a MainActivity si hay una sesión activa
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        // Referenciar los componentes del layout
        val registrarse = findViewById<TextView>(R.id.tvClickAqui)
        val etCorreo = findViewById<EditText>(R.id.etCorreo)
        val etContrasenna = findViewById<EditText>(R.id.etContrasenna)
        val btnIniciar = findViewById<Button>(R.id.btnIniciar)

        // Configurar el enlace de registro
        registrarse.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        // Configurar el botón de inicio de sesión
        btnIniciar.setOnClickListener {
            val email = etCorreo.text.toString().trim()
            val password = etContrasenna.text.toString().trim()

            if (validateInput(email, password, etCorreo, etContrasenna)) {
                loginUser(email, password)
            }
        }
    }

    // Validar entrada del usuario
    private fun validateInput(email: String, password: String, etCorreo: EditText, etContrasenna: EditText): Boolean {
        if (email.isEmpty()) {
            etCorreo.error = "El correo es obligatorio"
            etCorreo.requestFocus()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etCorreo.error = "Ingresa un correo válido"
            etCorreo.requestFocus()
            return false
        }
        if (password.isEmpty()) {
            etContrasenna.error = "La contraseña es obligatoria"
            etContrasenna.requestFocus()
            return false
        }
        return true
    }

    // Iniciar sesión en Firebase Authentication
    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Inicio de sesión exitoso
                    val intent = Intent(this@Login, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                } else {
                    // Error al iniciar sesión
                    Toast.makeText(
                        this@Login,
                        "Error al iniciar sesión: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}
