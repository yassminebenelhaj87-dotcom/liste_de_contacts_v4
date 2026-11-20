package com.example.liste_de_contacts_v2

import android.graphics.Color
import android.net.Uri
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(
    private var contacts: List<Contact>,
    private val onContactClick: (Contact) -> Unit,
    private val onCallClick: ((Contact) -> Unit)? = null
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    var fontSizeSp: Float = 16f
    var textColor: Int = Color.BLACK
    var isCallEnabled: Boolean = false

    class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvContactName)
        val tvPhone: TextView = view.findViewById(R.id.tvContactPhone)
        val ivPhoto: ImageView = view.findViewById(R.id.ivContactPhoto)
        val btnItemCall: ImageButton = view.findViewById(R.id.btnItemCall)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]
        holder.tvName.text = "${contact.nom} ${contact.prenom}"
        holder.tvPhone.text = contact.telephone

        // Application dynamique des préférences utilisateur
        holder.tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeSp)
        holder.tvName.setTextColor(textColor)
        holder.tvPhone.setTextSize(TypedValue.COMPLEX_UNIT_SP, (fontSizeSp - 2f).coerceAtLeast(10f))

        if (isCallEnabled) {
            holder.btnItemCall.visibility = View.VISIBLE
            holder.btnItemCall.setOnClickListener {
                onCallClick?.invoke(contact)
            }
        } else {
            holder.btnItemCall.visibility = View.GONE
            holder.btnItemCall.setOnClickListener(null)
        }

        if (!contact.photoUri.isNullOrEmpty()) {
            try {
                holder.ivPhoto.setImageURI(Uri.parse(contact.photoUri))
            } catch (_: Exception) {
                holder.ivPhoto.setImageResource(R.drawable.ic_avatar)
            }
        } else {
            holder.ivPhoto.setImageResource(R.drawable.ic_avatar)
        }

        holder.itemView.setOnClickListener {
            onContactClick(contact)
        }
    }

    override fun getItemCount(): Int = contacts.size

    fun updateData(newContacts: List<Contact>) {
        contacts = newContacts
        notifyDataSetChanged()
    }

    fun updatePreferences(fontSizeSp: Float, textColor: Int, isCallEnabled: Boolean) {
        this.fontSizeSp = fontSizeSp
        this.textColor = textColor
        this.isCallEnabled = isCallEnabled
        notifyDataSetChanged()
    }
}
