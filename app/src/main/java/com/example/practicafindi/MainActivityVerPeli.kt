package com.example.practicafindi

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main_home.*
import kotlinx.android.synthetic.main.activity_main_ver_peli.*
import java.lang.NullPointerException

private lateinit var database: DatabaseReference

private lateinit var databasepeliculas: DatabaseReference

var ac: Activity? = null

class MainActivityVerPeli : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_ver_peli)

        val nomP = intent.getStringExtra("nombre").toString()
        setUpReciclerView(nomP)
        buttonVerMas.setOnClickListener { salir() }
    }

    private fun salir() {
        onBackPressed()
    }

    fun setUpReciclerView(nombre: String) {
        val activi = this

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
                        val nom = snapshot.child(""+k).child("nombre").value as String
                        if(nom.equals(nombre)){
                            val cara = snapshot.child(""+k).child("caratula").value as String
                            val desc = snapshot.child(""+k).child("descripcion").value as String
                            val dura = snapshot.child(""+k).child("duracion").value as String
                            val valo = snapshot.child(""+k).child("valoracion").value as String
                            val enla = snapshot.child(""+k).child("enlace").value as String

                            Glide.with(activi).load(cara)
                                .into(
                                    photo_view
                                )
                            textViewNombre.setText(nom)
                            textViewDescripcion.setText(desc)
                            textViewDuracion.setText(dura)
                            textViewValoracion.setText(valo)
                            buttonVer.setOnClickListener {
                                val url = Uri.parse(enla)
                                val intent = Intent(Intent.ACTION_VIEW, url)
                                startActivity(intent)
                            }

                        }
                    }catch (e: NullPointerException){

                    }
                }
            }
        })
    }
}