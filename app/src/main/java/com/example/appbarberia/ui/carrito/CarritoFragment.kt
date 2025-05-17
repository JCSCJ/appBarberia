package com.example.appbarberia.ui.carrito

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appbarberia.databinding.FragmentCarritoBinding
import com.example.appbarberia.viewmodel.CarritoViewModel
import com.example.appbarberia.model.Producto

class CarritoFragment : Fragment() {

    private var _binding: FragmentCarritoBinding? = null
    private val binding get() = _binding!!

    private lateinit var carritoViewModel: CarritoViewModel
    private lateinit var adapter: CarritoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarritoBinding.inflate(inflater, container, false)

        carritoViewModel = ViewModelProvider(requireActivity())[CarritoViewModel::class.java]
        adapter = CarritoAdapter()

        binding.recyclerCarrito.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCarrito.adapter = adapter

        carritoViewModel.carrito.observe(viewLifecycleOwner) { productos ->
            adapter.actualizarLista(productos)
            actualizarResumen(productos)
        }

        binding.btnVaciarCarrito.setOnClickListener {
            carritoViewModel.vaciarCarrito()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val productos = carritoViewModel.carrito.value ?: emptyList()
        adapter.actualizarLista(productos)
        actualizarResumen(productos)
    }

    private fun actualizarResumen(productos: List<Producto>) {
        val subtotal = productos.sumOf { it.precio }
        val envio = 5.00
        val total = subtotal + envio

        binding.textSubtotal.text = "$${"%.2f".format(subtotal)}"
        binding.textEnvio.text = "$${"%.2f".format(envio)}"
        binding.textTotal.text = "$${"%.2f".format(total)}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
