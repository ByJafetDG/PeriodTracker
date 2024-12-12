package com.example.periodtracker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var tvNombreUsuario: TextView
    private lateinit var imgAjustes: ImageView

    private lateinit var db: FirebaseFirestore
    private lateinit var calendario: CalendarView
    private lateinit var tvDiaRegistrado: TextView
    private lateinit var tvDiaProximo: TextView
    private lateinit var btnRegistrarPeriodo: Button
    private lateinit var swDolorDeCabeza: Switch
    private lateinit var swColicos: Switch
    private lateinit var swAcne: Switch
    private lateinit var swFatiga: Switch
    private lateinit var swDolorDeSenos: Switch

    private var fechaSeleccionada: Long = 0L

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
        db = FirebaseFirestore.getInstance()

        // Referenciar componentes
        calendario = findViewById(R.id.calendario)
        tvDiaRegistrado = findViewById(R.id.tvDiaRegistrado)
        tvDiaProximo = findViewById(R.id.tvDiaProximo)
        btnRegistrarPeriodo = findViewById(R.id.btnRegistrarPeriodo)
        swDolorDeCabeza = findViewById(R.id.swDolorDeCabeza)
        swColicos = findViewById(R.id.swColicos)
        swAcne = findViewById(R.id.swAcne)
        swFatiga = findViewById(R.id.swFatiga)
        swDolorDeSenos = findViewById(R.id.swDolorDeSenos)

        // Referenciar el TextView
        tvNombreUsuario = findViewById(R.id.tvNombreUsuario)

        // Obtener el último registro
        mostrarUltimoRegistro()

        // Obtener la fecha seleccionada en el calendario
        calendario.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            fechaSeleccionada = calendar.timeInMillis
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            tvDiaRegistrado.text = "Día registrado: ${sdf.format(calendar.time)}"
        }

        // Registrar periodo al hacer clic en el botón
        btnRegistrarPeriodo.setOnClickListener {
            registrarPeriodo()
        }

        // Obtener y mostrar el nombre del usuario
        mostrarNombreUsuario()

        // Verificar si hay un usuario autenticado
        verificarSesion()

    }

    private fun mostrarUltimoRegistro() {
        val usuarioActual = auth.currentUser

        if (usuarioActual == null) {
            Toast.makeText(this, "No hay usuario en sesión", Toast.LENGTH_SHORT).show()
            return
        }

        // Consultar el último registro ordenado por fecha descendente
        db.collection("periodos")
            .whereEqualTo("usuarioId", usuarioActual.uid)
            .orderBy("fechaPeriodo", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val documento = documents.first()
                    val fechaPeriodo = documento.getLong("fechaPeriodo") ?: return@addOnSuccessListener
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                    // Mostrar el último día registrado
                    val fechaActual = Date(fechaPeriodo)
                    tvDiaRegistrado.text = "Último día registrado: ${sdf.format(fechaActual)}"

                    // Calcular y mostrar el próximo periodo
                    val calendarioProximo = Calendar.getInstance()
                    calendarioProximo.timeInMillis = fechaPeriodo
                    calendarioProximo.add(Calendar.DAY_OF_MONTH, 28)
                    val proximoPeriodo = calendarioProximo.time
                    tvDiaProximo.text = "Próximo periodo: ${sdf.format(proximoPeriodo)}"
                } else {
                    // Si no hay registros, mostrar mensaje predeterminado
                    tvDiaRegistrado.text = "Último día registrado: N/A"
                    tvDiaProximo.text = "Próximo periodo: N/A"
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar el último registro: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun registrarPeriodo() {
        val usuarioActual = auth.currentUser

        if (usuarioActual == null) {
            Toast.makeText(this, "No hay usuario en sesión", Toast.LENGTH_SHORT).show()
            return
        }

        val sintomas = mapOf(
            "DolorDeCabeza" to swDolorDeCabeza.isChecked,
            "Colicos" to swColicos.isChecked,
            "Acne" to swAcne.isChecked,
            "Fatiga" to swFatiga.isChecked,
            "DolorDeSenos" to swDolorDeSenos.isChecked
        )

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fechaActual = Date(fechaSeleccionada)

        val datosPeriodo = hashMapOf(
            "usuarioId" to usuarioActual.uid,
            "fechaPeriodo" to fechaSeleccionada,
            "sintomas" to sintomas
        )

        // Guardar en Firestore
        db.collection("periodos")
            .add(datosPeriodo)
            .addOnSuccessListener {
                Toast.makeText(this, "Periodo registrado con éxito", Toast.LENGTH_SHORT).show()

                // Calcular el próximo periodo (28 días después)
                val calendarioProximo = Calendar.getInstance()
                calendarioProximo.timeInMillis = fechaSeleccionada
                calendarioProximo.add(Calendar.DAY_OF_MONTH, 28)
                val proximoPeriodo = calendarioProximo.time
                tvDiaProximo.text = "Próximo periodo: ${sdf.format(proximoPeriodo)}"
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al registrar periodo: ${e.message}", Toast.LENGTH_LONG).show()
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
