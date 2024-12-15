package com.example.periodtracker

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private val RC_SIGN_IN = 9001  // Código para la actividad de inicio de sesión de Google

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        // Inicializar FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Configuración de Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))  // Asegúrate de agregar el client ID en strings.xml
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val ivLoginGoogle = findViewById<ImageView>(R.id.ivLoginGoogle)

        // Configurar el botón de Google
        ivLoginGoogle.setOnClickListener {
            signInWithGoogle()
        }

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

    // Método para iniciar sesión con Google
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    // Manejar el resultado del inicio de sesión con Google
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google Sign-In Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Autenticación con Firebase usando la cuenta de Google
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        if (account != null) {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this@Login, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@Login, "Authentication Failed.", Toast.LENGTH_SHORT)
                            .show()
                    }
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
