package com.example.liste_de_contacts_v2

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.liste_de_contacts_v2.data.PreferencesManager
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ListeContactsFragment : Fragment() {

    private val viewModel: ContactViewModel by activityViewModels()
    private lateinit var adapter: ContactAdapter
    private lateinit var etSearch: EditText
    private lateinit var tvEmptyState: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_liste_contacts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewContacts)
        etSearch = view.findViewById(R.id.etSearch)
        tvEmptyState = view.findViewById(R.id.tvEmptyState)
        val btnAddHeader = view.findViewById<ImageButton>(R.id.btnAddContactHeader)
        val btnSettings = view.findViewById<ImageButton>(R.id.btnSettings)
        val fabAddContact = view.findViewById<FloatingActionButton>(R.id.fabAddContact)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ContactAdapter(
            contacts = emptyList(),
            onContactClick = { contact -> openDetail(contact.id) },
            onCallClick = { contact -> makeDirectCall(contact) }
        )
        recyclerView.adapter = adapter

        viewModel.contacts.observe(viewLifecycleOwner) { contacts ->
            adapter.updateData(contacts)
            tvEmptyState.visibility = if (contacts.isEmpty()) View.VISIBLE else View.GONE
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchContacts(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        btnAddHeader.setOnClickListener { openDetail(null) }
        fabAddContact.setOnClickListener { openDetail(null) }

        btnSettings.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ParametresFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onResume() {
        super.onResume()
        // Chargement immédiat des préférences au retour sur le fragment
        applyUserPreferences()
        viewModel.loadContacts()
    }

    private fun applyUserPreferences() {
        val prefs = PreferencesManager(requireContext())
        adapter.updatePreferences(
            fontSizeSp = prefs.fontSizeSp,
            textColor = prefs.textColor,
            isCallEnabled = prefs.isCallFromListEnabled
        )
    }

    private fun makeDirectCall(contact: Contact) {
        val phone = contact.telephone.trim()
        if (phone.isNotEmpty()) {
            val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
            try {
                startActivity(callIntent)
            } catch (_: ActivityNotFoundException) {
                Toast.makeText(requireContext(), "Aucune application de téléphone disponible", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Numéro de téléphone vide", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openDetail(contactId: Long?) {
        val fragment = DetailContactFragment().apply {
            arguments = Bundle().apply {
                if (contactId != null && contactId != 0L) {
                    putLong("contact_id", contactId)
                }
            }
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
