package com.example.appbarberia.ui.carrito

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appbarberia.R
import com.example.appbarberia.model.Producto

class CarritoAdapter : RecyclerView.Adapter<CarritoAdapter.ViewHolder>() {

    private var productos = listOf<Producto>()

    fun actualizarLista(nuevaLista: List<Producto>) {
        productos = nuevaLista
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imagen: ImageView = view.findViewById(R.id.ivProductoCarrito)
        val nombre: TextView = view.findViewById(R.id.tvNombreCarrito)
        val precio: TextView = view.findViewById(R.id.tvPrecioCarrito)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto_carrito, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = productos[position]
        holder.imagen.setImageResource(producto.imagenResId)
        holder.nombre.text = producto.nombre
        holder.precio.text = "$${String.format("%.2f", producto.precio)}"
    }

    override fun getItemCount(): Int = productos.size
}
