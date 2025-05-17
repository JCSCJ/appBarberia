package com.example.appbarberia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.appbarberia.model.Producto

class CarritoViewModel : ViewModel() {

    private val _carrito = MutableLiveData<MutableList<Producto>>(mutableListOf())
    val carrito: LiveData<MutableList<Producto>> = _carrito

    fun agregarAlCarrito(producto: Producto) {
        _carrito.value?.add(producto)
        _carrito.value = _carrito.value // Forzar notificación
    }

    fun vaciarCarrito() {
        _carrito.value = mutableListOf()
    }
}
