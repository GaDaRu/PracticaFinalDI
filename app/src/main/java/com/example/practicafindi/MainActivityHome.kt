package com.example.practicafindi

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.practicafindi.modelo.pelicula
import com.example.rjuegos.RecyclerAdapter
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main_home.*


enum class ProviderType{
    BASIC,
    GOOGLE,
    FACEBOOK
}

private lateinit var database: DatabaseReference

private lateinit var databasepeliculas: DatabaseReference

private lateinit var eventListener: ValueEventListener

private lateinit var context: Context

var lista = ArrayList<pelicula>()

class MainActivityHome : AppCompatActivity(), RecyclerAdapter.OnPeliClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_home)

        val parametros = intent.extras
        val email = parametros?.getString("email")
        val provider = parametros?.getString("provider")

        setContext(this)

        setup(email ?: "", provider ?: "")

        buttonAnadirPeli.setOnClickListener { anadir() }

        setUpReciclerView()
    }

    fun setContext(con: Context) {
        context=con
    }

    private fun anadir() {
        val intent = Intent(this, MainActivityAnadir::class.java)
        startActivity(intent)
    }

    fun setUpReciclerView() {

        rv.layoutManager= LinearLayoutManager(this)
        rv.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        var ll = this

        database = Firebase.database.reference
        databasepeliculas = Firebase.database.reference.child("peliculas")

        databasepeliculas.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in 0..snapshot.childrenCount) {
                    val k = i.toInt()
                    try {
                        val pelicu = pelicula(
                            snapshot.child(k.toString()).child("nombre").value as String,
                            snapshot.child(k.toString()).child("caratula").value as String,
                            snapshot.child(k.toString()).child("descripcion").value as String,
                            snapshot.child(k.toString()).child("duracion").value as String,
                            snapshot.child(k.toString()).child("valoracion").value as String,
                            snapshot.child(k.toString()).child("enlace").value as String
                        )

                        lista.add(pelicu)
                    } catch (e: NullPointerException) {

                    }
                }
                rv.adapter = RecyclerAdapter(ll, lista, ll)
            }
        })
    }

    override fun onImagenClick(imagen: String) {
        val intent = Intent(this, MainActivityAnadir::class.java)
        startActivity(intent)
    }

    override fun onItemClick(nombre: String) {
        val intent = Intent(this, MainActivityVerPeli::class.java)
        intent.putExtra("nombre", nombre)
        startActivity(intent)
    }

    private fun setup(s: String, s1: String) {
        buttonsalir.setOnClickListener {
            val pref = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE).edit()
            pref.clear()
            pref.apply()

            if(s1 == ProviderType.FACEBOOK.name){
                LoginManager.getInstance().logOut()
            }

            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }
    }
}

