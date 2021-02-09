package com.example.practicafindi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.practicafindi.modelo.pelicula
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main_anadir.*

private lateinit var database: DatabaseReference

private lateinit var databasepeliculas: DatabaseReference

class MainActivityAnadir : AppCompatActivity() {
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_anadir)

        database = Firebase.database.reference

        buttonAnadir.setOnClickListener { anadir() }
    }

    private fun anadir() {
        database = Firebase.database.reference
        databasepeliculas = Firebase.database.reference.child("peliculas")

        databasepeliculas.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val peli = pelicula(
                    editTextNombre.text.toString(),
                    editTextCaratula.text.toString(),
                    editTextDescripcion.text.toString(),
                    editTextDuracion.text.toString(),
                    editTextValoracion.text.toString(),
                    editTextEnlace.text.toString()
                )

                database.child("peliculas").child(""+snapshot.childrenCount).setValue(peli)

                editTextNombre.setText("")
                editTextCaratula.setText("")
                editTextDescripcion.setText("")
                editTextDuracion.setText("")
                editTextValoracion.setText("")
                editTextEnlace.setText("")

                onBackPressed()
            }
        })
    }
}