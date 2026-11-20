package com.example.liste_de_contacts_v2

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.liste_de_contacts_v2.data.PreferencesManager
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class DetailContactFragment : Fragment() {

    private val viewModel: ContactViewModel by activityViewModels()

    private var contact: Contact? = null
    private var isNewContact: Boolean = false
    private var currentImageUri: String? = null

    private lateinit var ivPhoto: ImageView
    private lateinit var etNom: EditText
    private lateinit var etPrenom: EditText
    private lateinit var etTelephone: EditText
    private lateinit var etEmail: EditText
    private lateinit var etLatitude: EditText
    private lateinit var etLongitude: EditText

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val savedUri = copyUriToInternalStorage(uri)
            if (savedUri != null) {
                currentImageUri = savedUri.toString()
                ivPhoto.setImageURI(savedUri)
            } else {
                Toast.makeText(requireContext(), "Erreur lors de la copie de l'image", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Sélection d'image annulée", Toast.LENGTH_SHORT).show()
        }
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            val savedUri = saveBitmapToInternalStorage(bitmap)
            if (savedUri != null) {
                currentImageUri = savedUri.toString()
                ivPhoto.setImageBitmap(bitmap)
            } else {
                Toast.makeText(requireContext(), "Erreur lors de la sauvegarde de la photo", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Prise de photo annulée", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail_contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val contactId = arguments?.getLong("contact_id", -1L) ?: -1L
        isNewContact = contactId == -1L

        ivPhoto = view.findViewById(R.id.ivDetailPhoto)
        etNom = view.findViewById(R.id.etNom)
        etPrenom = view.findViewById(R.id.etPrenom)
        etTelephone = view.findViewById(R.id.etTelephone)
        etEmail = view.findViewById(R.id.etEmail)
        etLatitude = view.findViewById(R.id.etLatitude)
        etLongitude = view.findViewById(R.id.etLongitude)

        applyUserPreferences()

        val tvDetailTitle = view.findViewById<TextView>(R.id.tvDetailTitle)
        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)
        val btnDelete = view.findViewById<ImageButton>(R.id.btnDeleteContact)
        val ivCameraBadge = view.findViewById<ImageView>(R.id.ivCameraBadge)
        val btnGallery = view.findViewById<Button>(R.id.btnGallery)
        val btnCamera = view.findViewById<Button>(R.id.btnCamera)
        val btnCall = view.findViewById<ImageButton>(R.id.btnCall)
        val btnSendEmail = view.findViewById<ImageButton>(R.id.btnSendEmail)
        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnMap = view.findViewById<Button>(R.id.btnMap)

        tvDetailTitle.text = if (isNewContact) "Nouveau contact" else "Détail du contact"

        if (isNewContact) {
            contact = Contact(id = 0L, nom = "", prenom = "", telephone = "")
            btnDelete.visibility = View.GONE
            bindContactData()
        } else {
            lifecycleScope.launch {
                val loadedContact = viewModel.getContactById(contactId)
                if (loadedContact == null) {
                    Toast.makeText(requireContext(), "Erreur : Contact introuvable", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                    return@launch
                }
                contact = loadedContact.copy()
                currentImageUri = contact?.photoUri
                bindContactData()
            }
        }

        btnBack.setOnClickListener { parentFragmentManager.popBackStack() }
        btnCancel.setOnClickListener { parentFragmentManager.popBackStack() }

        btnDelete.setOnClickListener {
            if (!isNewContact && contact != null) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Confirmation de suppression")
                    .setMessage("Voulez-vous vraiment supprimer le contact ${contact?.nom} ${contact?.prenom} ?")
                    .setPositiveButton("Supprimer") { _, _ ->
                        viewModel.deleteContact(contact!!.id) {
                            Toast.makeText(requireContext(), "Contact supprimé", Toast.LENGTH_SHORT).show()
                            parentFragmentManager.popBackStack()
                        }
                    }
                    .setNegativeButton("Annuler", null)
                    .show()
            } else {
                parentFragmentManager.popBackStack()
            }
        }

        ivCameraBadge.setOnClickListener { launchCamera() }
        btnCamera.setOnClickListener { launchCamera() }
        btnGallery.setOnClickListener { galleryLauncher.launch("image/*") }

        btnCall.setOnClickListener {
            val phone = etTelephone.text?.toString()?.trim() ?: ""
            if (phone.isNotEmpty()) {
                val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                try {
                    startActivity(callIntent)
                } catch (_: ActivityNotFoundException) {
                    Toast.makeText(requireContext(), "Aucune application de téléphone disponible", Toast.LENGTH_SHORT).show()
                }
            } else {
                etTelephone.error = "Veuillez entrer un numéro de téléphone"
            }
        }

        btnSendEmail.setOnClickListener {
            val email = etEmail.text?.toString()?.trim() ?: ""
            if (email.isNotEmpty()) {
                val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
                try {
                    startActivity(emailIntent)
                } catch (_: ActivityNotFoundException) {
                    Toast.makeText(requireContext(), "Aucune application de messagerie disponible", Toast.LENGTH_SHORT).show()
                }
            } else {
                etEmail.error = "Veuillez entrer une adresse e-mail"
            }
        }

        btnMap.setOnClickListener {
            val latText = etLatitude.text.toString().trim()
            val lngText = etLongitude.text.toString().trim()
            val lat = latText.toDoubleOrNull()
            val lng = lngText.toDoubleOrNull()

            if (lat == null || lng == null) {
                Toast.makeText(requireContext(), "Coordonnées GPS absentes ou invalides", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (lat < -90.0 || lat > 90.0) {
                etLatitude.error = "Latitude doit être entre -90 et 90"
                return@setOnClickListener
            }
            if (lng < -180.0 || lng > 180.0) {
                etLongitude.error = "Longitude doit être entre -180 et 180"
                return@setOnClickListener
            }

            val fragment = CarteContactFragment().apply {
                arguments = Bundle().apply {
                    putDouble("latitude", lat)
                    putDouble("longitude", lng)
                    putString("nom", "${etNom.text} ${etPrenom.text}")
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        btnSave.setOnClickListener {
            saveContact()
        }
    }

    private fun applyUserPreferences() {
        val prefs = PreferencesManager(requireContext())
        val sizeSp = prefs.fontSizeSp
        val color = prefs.textColor

        etNom.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeSp)
        etNom.setTextColor(color)

        etPrenom.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeSp)
        etPrenom.setTextColor(color)

        etTelephone.setTextSize(TypedValue.COMPLEX_UNIT_SP, (sizeSp - 2f).coerceAtLeast(12f))
    }

    private fun bindContactData() {
        val c = contact ?: return
        etNom.setText(c.nom)
        etPrenom.setText(c.prenom)
        etTelephone.setText(c.telephone)
        etEmail.setText(c.email ?: "")
        etLatitude.setText(c.latitude?.toString() ?: "")
        etLongitude.setText(c.longitude?.toString() ?: "")

        val uriStr = currentImageUri ?: c.photoUri
        if (!uriStr.isNullOrEmpty()) {
            try {
                ivPhoto.setImageURI(Uri.parse(uriStr))
            } catch (_: Exception) {
                ivPhoto.setImageResource(R.drawable.ic_avatar)
            }
        } else {
            ivPhoto.setImageResource(R.drawable.ic_avatar)
        }
    }

    private fun launchCamera() {
        try {
            cameraLauncher.launch(null)
        } catch (_: Exception) {
            Toast.makeText(requireContext(), "Impossible d'ouvrir l'appareil photo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveContact() {
        val nom = etNom.text?.toString()?.trim() ?: ""
        val prenom = etPrenom.text?.toString()?.trim() ?: ""
        val phone = etTelephone.text?.toString()?.trim() ?: ""
        val email = etEmail.text?.toString()?.trim() ?: ""

        val latText = etLatitude.text?.toString()?.trim() ?: ""
        val lngText = etLongitude.text?.toString()?.trim() ?: ""

        var lat: Double? = null
        var lng: Double? = null

        if (nom.isEmpty()) {
            etNom.error = "Le nom est obligatoire"
            return
        }
        if (prenom.isEmpty()) {
            etPrenom.error = "Le prénom est obligatoire"
            return
        }
        if (phone.isEmpty()) {
            etTelephone.error = "Le numéro de téléphone est obligatoire"
            return
        }

        if (latText.isNotEmpty()) {
            lat = latText.toDoubleOrNull()
            if (lat == null || lat < -90.0 || lat > 90.0) {
                etLatitude.error = "Latitude valide entre -90 et 90 requise"
                return
            }
        }

        if (lngText.isNotEmpty()) {
            lng = lngText.toDoubleOrNull()
            if (lng == null || lng < -180.0 || lng > 180.0) {
                etLongitude.error = "Longitude valide entre -180 et 180 requise"
                return
            }
        }

        if ((lat != null && lng == null) || (lat == null && lng != null)) {
            Toast.makeText(requireContext(), "Veuillez renseigner la latitude ET la longitude", Toast.LENGTH_SHORT).show()
            return
        }

        val c = contact ?: Contact(nom = nom, prenom = prenom, telephone = phone)
        c.nom = nom
        c.prenom = prenom
        c.telephone = phone
        c.email = if (email.isNotEmpty()) email else null
        c.latitude = lat
        c.longitude = lng
        c.photoUri = currentImageUri

        if (isNewContact || c.id == 0L) {
            viewModel.addContact(c) {
                Toast.makeText(requireContext(), "Contact enregistré", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
        } else {
            viewModel.updateContact(c) {
                Toast.makeText(requireContext(), "Contact mis à jour", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun copyUriToInternalStorage(uri: Uri): Uri? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri) ?: return null
            val file = File(requireContext().filesDir, "photo_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun saveBitmapToInternalStorage(bitmap: Bitmap): Uri? {
        return try {
            val file = File(requireContext().filesDir, "photo_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
