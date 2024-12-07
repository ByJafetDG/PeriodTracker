package com.example.periodtracker

import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var btnRegisterPeriod: Button
    private var selectedDate: Long = 0L // Guardará la fecha seleccionada en milisegundos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        calendarView = findViewById(R.id.calendario)
        btnRegisterPeriod = findViewById(R.id.btnRegistrarPeriodo)

        // Listener para seleccionar una fecha
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            selectedDate = calendar.timeInMillis
        }

        // Al hacer clic en "Registrar periodo"
        btnRegisterPeriod.setOnClickListener {
            if (selectedDate != 0L) {
                // Cambiar el color de fondo (esto es más simbólico para el calendario nativo)
                Toast.makeText(this, "Periodo registrado para la fecha seleccionada", Toast.LENGTH_SHORT).show()
                // Aquí podrías guardar la fecha en tu base de datos o lógica
                highlightDate()
            } else {
                Toast.makeText(this, "Por favor selecciona una fecha", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Método para resaltar la fecha seleccionada
    private fun highlightDate() {
        val selectedDateFormatted = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(selectedDate))
        Toast.makeText(this, "Fecha marcada: $selectedDateFormatted", Toast.LENGTH_SHORT).show()
        // Nota: El calendario nativo no permite cambiar visualmente días específicos directamente.
        // Podrías llevar un registro lógico o usar un diseño personalizado.
    }
}
