package com.example.desafio3dsm.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.desafio3dsm.R
import com.example.desafio3dsm.model.Recurso
import com.google.android.material.chip.Chip

class RecursoAdapter(
    private var lista: List<Recurso>,
    private var favoritos: Set<String>,
    private val onFavoritoClick: (Recurso) -> Unit,
    private val onRatingClick: (Recurso, Float) -> Unit,
    private val onItemClick: (Recurso) -> Unit
) : RecyclerView.Adapter<RecursoAdapter.RecursoViewHolder>() {

    inner class RecursoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivImagen: ImageView = itemView.findViewById(R.id.ivImagen)
        val chipTipo: Chip = itemView.findViewById(R.id.chipTipo)
        val tvTitulo: TextView = itemView.findViewById(R.id.tvTitulo)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        val tvRating: TextView = itemView.findViewById(R.id.tvRating)
        val btnFavorito: ImageButton = itemView.findViewById(R.id.btnFavorito)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecursoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recurso, parent, false)
        return RecursoViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecursoViewHolder, position: Int) {
        val recurso = lista[position]

        holder.tvTitulo.text = recurso.titulo
        holder.tvDescripcion.text = recurso.descripcion
        holder.chipTipo.text = recurso.tipo
        holder.tvRating.text = "(${recurso.rating}★ · ${recurso.totalRating} votos)"
        holder.ratingBar.rating = recurso.rating

        // Cargar imagen con Glide
        Glide.with(holder.itemView.context)
            .load(recurso.imagen)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_gallery)
            .into(holder.ivImagen)

        // Estado favorito
        val esFavorito = favoritos.contains(recurso.id)
        holder.btnFavorito.setImageResource(
            if (esFavorito) android.R.drawable.btn_star_big_on
            else android.R.drawable.btn_star_big_off
        )

        // Listeners
        holder.btnFavorito.setOnClickListener { onFavoritoClick(recurso) }
        holder.itemView.setOnClickListener { onItemClick(recurso) }
        holder.ratingBar.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser) onRatingClick(recurso, rating)
        }
    }

    override fun getItemCount() = lista.size

    fun actualizarLista(nuevaLista: List<Recurso>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }

    fun actualizarFavoritos(nuevosFavoritos: Set<String>) {
        favoritos = nuevosFavoritos
        notifyDataSetChanged()
    }
}