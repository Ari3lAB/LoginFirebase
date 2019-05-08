package aramburo.ariel.loginfirebase

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.OptionalPendingResult
import com.google.android.gms.common.api.ResultCallback
import android.support.annotation.NonNull
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class MainActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {
    lateinit private var firebaseAuth: FirebaseAuth
    lateinit private var firebaseAuthListener: FirebaseAuth.AuthStateListener
    override fun onConnectionFailed(p0: ConnectionResult) {}

    var clienteApiGoogle: GoogleApiClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        clienteApiGoogle = GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()
        btn_cerrarSesion.setOnClickListener() {
            logOut()
        }
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val usuario = firebaseAuth.currentUser
            if (usuario != null) {
                establecerDatos(usuario)
            } else {
                irALogIn()
            }
        }
    }

    private fun establecerDatos(usuario: FirebaseUser) {
        txt_nombre.text = usuario.displayName
        txt_correo.text = usuario.email
        Glide.with(this).load(usuario.photoUrl).into(img_perfil)
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(firebaseAuthListener)
    }


    private fun irALogIn() {
        val intent: Intent = Intent(this, Login::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    fun logOut() {
        firebaseAuth.signOut()
        Auth.GoogleSignInApi.signOut(clienteApiGoogle).setResultCallback { status ->
            if (status.isSuccess) {
                irALogIn()
            } else {
                Toast.makeText(applicationContext, "Error al cerrar sesión.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (firebaseAuthListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthListener)
        }
    }
}
