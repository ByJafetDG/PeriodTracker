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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class Register : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private val RC_SIGN_IN = 9001  // Código para la actividad de inicio de sesión de Google

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

        // Configuración de Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))  // Asegúrate de agregar el client ID en strings.xml
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Referenciar los componentes del layout
        val etCorreo = findViewById<EditText>(R.id.etCorreo)
        val etContrasenna = findViewById<EditText>(R.id.etContrasenna)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)
        val ivRegisterGoogle = findViewById<ImageView>(R.id.ivRegisterGoogle)

        // Configurar el botón de Google
        ivRegisterGoogle.setOnClickListener {
            signInWithGoogle()
        }

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

    // Registrar al usuario con correo y contraseña
    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Inicia sesión automáticamente
                    val intent = Intent(this@Register, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                } else {
                    Toast.makeText(this@Register, "Error al registrar: ${task.exception?.message}", Toast.LENGTH_LONG).show()
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
                        val intent = Intent(this@Register, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@Register, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
