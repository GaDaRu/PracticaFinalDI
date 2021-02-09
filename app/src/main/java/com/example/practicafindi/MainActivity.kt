package com.example.practicafindi

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.practicafindi.modelo.user
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val GOOGLE_SIGN_IN = 100

    private val callbackManager = CallbackManager.Factory.create()

    private lateinit var database: DatabaseReference

    private lateinit var databaseusuario: DatabaseReference

    private lateinit var eventListener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val analytics:FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Entró")
        analytics.logEvent("PantallaInicial", bundle)

        database = Firebase.database.reference

        iniciada()
        setUp()
    }

    private fun setUp(){
        buttonIniciar.setOnClickListener {
            if(editTextTextEmailAddress.text.isNotEmpty() && editTextTextPassword.text.isNotEmpty()){
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    editTextTextEmailAddress.text.toString(),
                    editTextTextPassword.text.toString()
                ).addOnCompleteListener{
                    if(it.isSuccessful){
                        showHome(editTextTextEmailAddress.text.toString() ?: "", ProviderType.BASIC)
                    }else{
                        showError()
                    }
                }
            }
        }

        buttonRegistrar.setOnClickListener {
            if(editTextTextEmailAddress.text.isNotEmpty() && editTextTextPassword.text.isNotEmpty()){
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    editTextTextEmailAddress.text.toString(),
                    editTextTextPassword.text.toString()
                ).addOnCompleteListener{
                    if(it.isSuccessful){
                        showLogin(editTextTextEmailAddress.text.toString() ?: "", ProviderType.BASIC)
                    }else{
                        showError()
                    }
                }
            }
        }

        buttonGoogle.setOnClickListener {
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(
                getString(
                    R.string.default_web_client_id
                )
            ).requestEmail().build()

            val googleClient = GoogleSignIn.getClient(this, googleConf)

            googleClient.signOut()

            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }
    }

    private fun iniciada(){
        val prefs: SharedPreferences = getSharedPreferences(
            getString(R.string.pref_file),
            Context.MODE_PRIVATE
        )
        val email = prefs.getString("email", null)
        val provider = prefs.getString("provider", null)
        if(email != null && provider != null){
            showHome(email, ProviderType.valueOf(provider))
        }
    }

    private fun showError() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("ERROR")
        builder.setMessage("Error")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == GOOGLE_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)

                if(account != null){

                    database.child("usuarios").addListenerForSingleValueEvent(
                        object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {

                                val usuario = user()
                                usuario.correo = dataSnapshot.child("hola@gmail").child("correo").value as String
                                val usu = usuario.correo.split(".")
                                if(usu[0].equals(account.email ?: "")){
                                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                                        if(it.isSuccessful){
                                            showHome(account.email ?: "", ProviderType.GOOGLE)
                                        }else{
                                            showError()
                                        }
                                    }
                                }else{
                                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                                        if(it.isSuccessful){
                                            showLogin(account.email ?: "", ProviderType.GOOGLE)
                                        }else{
                                            showError()
                                        }
                                    }
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                            }
                        })
                }
            }catch (e: ApiException){
                showError()
            }


        }
    }

    private fun showHome(email: String, provider: ProviderType) {
        val homeIntent = Intent(this, MainActivityHome::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }

    private fun showLogin(email: String, provider: ProviderType) {
        var usuario = user()
        usuario.id = "0"
        usuario.correo = email
        val corre = usuario.correo.split(".")

        database.child("usuarios").child(corre[0]).setValue(usuario)

        val homeIntent = Intent(this, MainActivityHome::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }
}