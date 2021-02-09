package com.example.rjuegos

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.practicafindi.R
import com.example.practicafindi.modelo.pelicula
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.pelicula_row.view.*


class RecyclerAdapter(val context: Context, val listaPeliculas:ArrayList<pelicula>, private val itemClickListener: OnPeliClickListener):RecyclerView.Adapter<BaseViewHolder<*>>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return PeliculasViewHolder(LayoutInflater.from(context).inflate(R.layout.pelicula_row, parent, false))
    }

    interface OnPeliClickListener{
        fun onImagenClick(imagen:String)
        fun onItemClick(nombre:String)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        if(holder is PeliculasViewHolder){
            holder.bind(listaPeliculas[position], position)
        }
    }

    override fun getItemCount(): Int = listaPeliculas.size

    inner class PeliculasViewHolder(itemView: View):BaseViewHolder<pelicula>(itemView){
        override fun bind(item: pelicula, position: Int) {
            Glide.with(context).load(item.caratula).into(itemView.profile_image)
            itemView.Nombre.text = item.nombre
            itemView.Duracion.text = item.duracion


            itemView.setOnClickListener{
                itemClickListener.onItemClick(item.nombre)
            }
        }
    }
}