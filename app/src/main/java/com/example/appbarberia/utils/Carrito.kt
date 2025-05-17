package com.example.appbarberia.utils

import com.example.appbarberia.model.Producto

object Carrito {
    private val productosEnCarrito = mutableListOf<Producto>()

    fun agregarProducto(producto: Producto) {
        productosEnCarrito.add(producto)
    }

    fun obtenerProductos(): List<Producto> = productosEnCarrito

    fun limpiarCarrito() {
        productosEnCarrito.clear()
    }
}
