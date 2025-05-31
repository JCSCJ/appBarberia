package com.example.appbarberia.ui.notifications

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.appbarberia.LoginActivity
import com.example.appbarberia.R
import com.example.appbarberia.databinding.FragmentAccountBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class NotificationsFragment : Fragment() {

    private val storageRef = FirebaseStorage.getInstance().reference
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "sin_id"

    private var photoUri: Uri? = null

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { subirImagenAFirebase(it) }
        }

    private val takePhotoLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && photoUri != null) {
                subirImagenAFirebase(photoUri!!)
            }
        }

    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                tomarFotoConCamara()
            } else {
                showPermissionDeniedToast("Se necesita permiso de la cámara para tomar fotos")
            }
        }

    private val requestMediaPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                pickImageLauncher.launch("image/*")
            } else {
                showPermissionDeniedToast("Se necesita permiso para acceder a las imágenes")
            }
        }

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)

        binding.btnCerrarSesion.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }

        binding.imgPerfil.setOnClickListener {
            mostrarOpcionesFoto()
        }
        cargarFotoDesdeFirebase()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun mostrarOpcionesFoto() {
        val opciones = arrayOf("Tomar foto", "Elegir de galería")
        AlertDialog.Builder(requireContext())
            .setTitle("Cambiar foto de perfil")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> checkCameraPermission()
                    1 -> checkMediaPermission()
                }
            }.show()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            tomarFotoConCamara()
        } else {
            requestCameraPermission.launch(Manifest.permission.CAMERA)
        }
    }

    private fun checkMediaPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            pickImageLauncher.launch("image/*")
        } else {
            requestMediaPermission.launch(permission)
        }
    }

    private fun tomarFotoConCamara() {
        try {
            val file = File.createTempFile(
                "foto_${System.currentTimeMillis()}",
                ".jpg",
                requireContext().cacheDir
            )
            photoUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.provider",
                file
            )
            takePhotoLauncher.launch(photoUri)
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Error al acceder a la cámara: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun subirImagenAFirebase(uri: Uri) {
        val ref = storageRef.child("usuarios/$userId/foto_perfil.jpg")
        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { downloadUrl ->
                    Glide.with(requireContext())
                        .load(downloadUrl)
                        .placeholder(R.drawable.placeholder_avatar)
                        .error(R.drawable.error_avatar)
                        .into(binding.imgPerfil)
                    guardarUrlEnFirestore(downloadUrl.toString())
                    Toast.makeText(
                        requireContext(),
                        "Foto de perfil actualizada",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Error al subir la imagen: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun guardarUrlEnFirestore(url: String) {
        FirebaseFirestore.getInstance().collection("usuarios")
            .document(userId)
            .set(mapOf("fotoPerfil" to url), SetOptions.merge())
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Error al guardar la URL de la imagen",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun cargarFotoDesdeFirebase() {
        FirebaseFirestore.getInstance().collection("usuarios")
            .document(userId)
            .get()
            .addOnSuccessListener { doc ->
                val url = doc.getString("fotoPerfil")
                if (!url.isNullOrEmpty()) {
                    Glide.with(requireContext())
                        .load(url)
                        .placeholder(R.drawable.placeholder_avatar)
                        .error(R.drawable.error_avatar)
                        .into(binding.imgPerfil)
                }
            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Error al cargar la foto de perfil",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun showPermissionDeniedToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }


}