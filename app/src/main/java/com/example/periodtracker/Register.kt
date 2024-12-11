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

class Register : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        val register = findViewById<TextView>(R.id.tvClickAqui2)

        register.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        // Inicializar FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Referenciar los componentes del layout
        val etCorreo = findViewById<EditText>(R.id.etCorreo)
        val etContrasenna = findViewById<EditText>(R.id.etContrasenna)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)

        // Configurar el botón de registro
        btnRegistrar.setOnClickListener {
            val email = etCorreo.text.toString().trim()
            val password = etContrasenna.text.toString().trim()

            if (validateInput(email, password, etCorreo, etContrasenna)) {
                registerUser(email, password)
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
        if (password.length < 6) {
            etContrasenna.error = "La contraseña debe tener al menos 6 caracteres"
            etContrasenna.requestFocus()
            return false
        }
        return true
    }

    // Registrar al usuario en Firebase Authentication
    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Inicia sesión automáticamente
                    val intent = Intent(this@Register, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this@Register,
                        "Error al registrar: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}
