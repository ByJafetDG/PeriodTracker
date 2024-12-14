package com.example.periodtracker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class Historial : AppCompatActivity() {

    private lateinit var ivAtrasH: ImageView
    private lateinit var lvHistorial: ListView
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var tvNombreUsuario: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.historial)

        // Referenciar el TextView
        tvNombreUsuario = findViewById(R.id.tvNombreDeUsuarioH)
        // Obtener y mostrar el nombre del usuario
        mostrarNombreUsuario()

        // Inicializar la propiedad ivAtrasH
        ivAtrasH = findViewById(R.id.ivAtrasH)
        lvHistorial = findViewById(R.id.lvHistorial)

        // Configurar botón de retroceso
        ivAtrasH.setOnClickListener {
            val intent = Intent(this, Perfil::class.java)
            startActivity(intent)
            finish()
        }

        // Cargar historial
        cargarHistorial()
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

    private fun cargarHistorial() {
        val usuarioActual = auth.currentUser
        if (usuarioActual != null) {
            val userId = usuarioActual.uid

            // Consultar la colección de periodos para este usuario
            db.collection("periodos")
                .whereEqualTo("usuarioId", userId)
                .get()
                .addOnSuccessListener { documentos ->
                    val listaHistorial = ArrayList<String>()

                    for (documento in documentos) {
                        val fechaPeriodo = documento.getLong("fechaPeriodo") ?: continue
                        val sintomasMap = documento.get("sintomas") as? Map<*, *>

                        // Formatear la fecha
                        val fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            .format(Date(fechaPeriodo))

                        // Construir la descripción de los síntomas
                        val sintomas = sintomasMap?.entries
                            ?.joinToString { "${it.key}: ${if (it.value == true) "Sí" else "No"}" }
                            ?: "Sin datos"

                        listaHistorial.add("Fecha: $fecha\nSíntomas:\n$sintomas")
                    }

                    // Mostrar los datos en el ListView
                    val adaptador = ArrayAdapter(
                        this,
                        android.R.layout.simple_list_item_1,
                        listaHistorial
                    )
                    lvHistorial.adapter = adaptador
                }
                .addOnFailureListener { e ->
                    Log.e("Historial", "Error al obtener historial", e)
                    Toast.makeText(this, "Error al cargar el historial", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }
    }
}
