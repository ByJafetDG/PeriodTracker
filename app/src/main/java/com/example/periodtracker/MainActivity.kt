package com.example.periodtracker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var tvNombreUsuario: TextView
    private lateinit var imgAjustes: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imgAjustes = findViewById<ImageView>(R.id.imgAjustes)

        imgAjustes.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, Perfil::class.java)
            startActivity(intent)
            finish()
        })

        // Inicializar FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Referenciar el TextView
        tvNombreUsuario = findViewById(R.id.tvNombreUsuario)

        // Obtener y mostrar el nombre del usuario
        mostrarNombreUsuario()

        // Verificar si hay un usuario autenticado
        verificarSesion()

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

    private fun verificarSesion() {
        val usuarioActual = auth.currentUser
        if (usuarioActual != null) {
            // Si hay un usuario, mostrar su nombre
            val correo = usuarioActual.email
            val nombreUsuario = correo?.substringBefore("@")
            tvNombreUsuario.text = nombreUsuario
        } else {
            // Si no hay usuario, redirigir al LoginActivity
            redirigirAlLogin()
        }
    }

    private fun redirigirAlLogin() {
        Toast.makeText(this, "Por favor, inicie sesión", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()
    }

}
