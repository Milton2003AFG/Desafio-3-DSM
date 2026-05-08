package com.example.desafio3dsm.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.desafio3dsm.R
import com.example.desafio3dsm.model.Recurso
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip

class RecursoDocenteAdapter(
    private var lista: List<Recurso>,
    private val onEditar: (Recurso) -> Unit,
    private val onEliminar: (Recurso) -> Unit
) : RecyclerView.Adapter<RecursoDocenteAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivImagen: ImageView = itemView.findViewById(R.id.ivImagen)
        val chipTipo: Chip = itemView.findViewById(R.id.chipTipo)
        val tvTitulo: TextView = itemView.findViewById(R.id.tvTitulo)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        val btnEditar: MaterialButton = itemView.findViewById(R.id.btnEditar)
        val btnEliminar: MaterialButton = itemView.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recurso_docente, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recurso = lista[position]

        holder.tvTitulo.text = recurso.titulo
        holder.tvDescripcion.text = recurso.descripcion
        holder.chipTipo.text = recurso.tipo

        Glide.with(holder.itemView.context)
            .load(recurso.imagen)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_gallery)
            .into(holder.ivImagen)

        holder.btnEditar.setOnClickListener { onEditar(recurso) }
        holder.btnEliminar.setOnClickListener { onEliminar(recurso) }
    }

    override fun getItemCount() = lista.size

    fun actualizarLista(nuevaLista: List<Recurso>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}