package com.example.appbarberia.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.appbarberia.R
import com.example.appbarberia.databinding.FragmentHomeBinding
import com.example.appbarberia.model.Producto
import com.example.appbarberia.viewmodel.CarritoViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var carritoViewModel: CarritoViewModel

    private val listaProductos = listOf(
        Producto("Máquina Profesional", 59.99, R.drawable.barber_professional_machine),
        Producto("Tijeras de Barbero", 24.99, R.drawable.barber_scissors),
        Producto("Crema de Afeitar", 9.99, R.drawable.shave_cream),
        Producto("Afeitadora Clásica", 34.99, R.drawable.classic_shave),
        Producto("Kit Profesional", 89.99, R.drawable.professional_barber_kit)
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        carritoViewModel = ViewModelProvider(requireActivity())[CarritoViewModel::class.java]

        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = ProductoAdapter(listaProductos) { producto ->
            carritoViewModel.agregarAlCarrito(producto)
            Toast.makeText(requireContext(), "Agregado: ${producto.nombre}", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
