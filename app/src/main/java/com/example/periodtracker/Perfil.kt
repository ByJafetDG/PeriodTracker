package com.example.periodtracker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Perfil : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var tvNombreUsuario: TextView
    private lateinit var btnCerrarSesion: Button
    private lateinit var ivAtras: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.perfil)

        // Inicializar FirebaseAuth
        auth = FirebaseAuth.getInstance()

        ivAtras = findViewById<ImageView>(R.id.ivAtras)

        ivAtras.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        })

        btnCerrarSesion = findViewById<Button>(R.id.btnCerrarSesion)
        // Referenciar el TextView
        tvNombreUsuario = findViewById(R.id.tvNombreDeUsuario)

        // Obtener y mostrar el nombre del usuario
        mostrarNombreUsuario()

        // Configurar el botón de cerrar sesión
        btnCerrarSesion.setOnClickListener {
            cerrarSesion()
        }

    }

    private fun mostrarNombreUsuario() {
        val usuarioActual = auth.currentUser
        if (usuarioActual != null) {
            // Obtener el correo y extraer el texto antes del @
            val correo = usuarioActual.email
            val nombreUsuario = correo?.substringBefore("@")
            // Mostrar el nombre en el TextView
            tvNombreUsuario.text = nombreUsuario
        } else {
            // Si no hay usuario, mostrar mensaje de error
            Toast.makeText(this, "No hay usuario autenticado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cerrarSesion() {
        auth.signOut()
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
        // Redirigir al LoginActivity
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()
    }

}