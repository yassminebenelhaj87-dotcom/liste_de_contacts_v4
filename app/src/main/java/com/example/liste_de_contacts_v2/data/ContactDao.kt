package com.example.liste_de_contacts_v2.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.liste_de_contacts_v2.Contact
import com.example.liste_de_contacts_v2.data.ContactContract.ContactEntry

/**
 * DAO (Data Access Object) gérant l'accès SQL à la table contacts.
 * Seule classe contenant du SQL.
 */
class ContactDao(context: Context) {

    private val dbHelper = ContactDbHelper.getInstance(context)

    fun insert(contact: Contact): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(ContactEntry.COLUMN_NOM, contact.nom)
            put(ContactEntry.COLUMN_PRENOM, contact.prenom)
            put(ContactEntry.COLUMN_TELEPHONE, contact.telephone)
            put(ContactEntry.COLUMN_EMAIL, contact.email)
            put(ContactEntry.COLUMN_PHOTO_URI, contact.photoUri)
            if (contact.latitude != null) put(ContactEntry.COLUMN_LATITUDE, contact.latitude) else putNull(ContactEntry.COLUMN_LATITUDE)
            if (contact.longitude != null) put(ContactEntry.COLUMN_LONGITUDE, contact.longitude) else putNull(ContactEntry.COLUMN_LONGITUDE)
        }
        val id = db.insert(ContactEntry.TABLE_NAME, null, values)
        if (id != -1L) {
            contact.id = id
        }
        return id
    }

    fun update(contact: Contact): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(ContactEntry.COLUMN_NOM, contact.nom)
            put(ContactEntry.COLUMN_PRENOM, contact.prenom)
            put(ContactEntry.COLUMN_TELEPHONE, contact.telephone)
            put(ContactEntry.COLUMN_EMAIL, contact.email)
            put(ContactEntry.COLUMN_PHOTO_URI, contact.photoUri)
            if (contact.latitude != null) put(ContactEntry.COLUMN_LATITUDE, contact.latitude) else putNull(ContactEntry.COLUMN_LATITUDE)
            if (contact.longitude != null) put(ContactEntry.COLUMN_LONGITUDE, contact.longitude) else putNull(ContactEntry.COLUMN_LONGITUDE)
        }

        return db.update(
            ContactEntry.TABLE_NAME,
            values,
            "${ContactEntry.COLUMN_ID} = ?",
            arrayOf(contact.id.toString())
        )
    }

    fun delete(id: Long): Int {
        val db = dbHelper.writableDatabase
        return db.delete(
            ContactEntry.TABLE_NAME,
            "${ContactEntry.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
    }

    fun getAll(): List<Contact> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            ContactEntry.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            "${ContactEntry.COLUMN_NOM} COLLATE NOCASE ASC, ${ContactEntry.COLUMN_PRENOM} COLLATE NOCASE ASC"
        )
        return cursor.use { extractContactsFromCursor(it) }
    }

    fun getById(id: Long): Contact? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            ContactEntry.TABLE_NAME,
            null,
            "${ContactEntry.COLUMN_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        return cursor.use {
            if (it.moveToFirst()) {
                extractContactFromCursor(it)
            } else {
                null
            }
        }
    }

    fun search(query: String): List<Contact> {
        val db = dbHelper.readableDatabase
        val pattern = "%$query%"
        val selection = "${ContactEntry.COLUMN_NOM} LIKE ? OR ${ContactEntry.COLUMN_PRENOM} LIKE ? OR ${ContactEntry.COLUMN_TELEPHONE} LIKE ?"
        val selectionArgs = arrayOf(pattern, pattern, pattern)

        val cursor = db.query(
            ContactEntry.TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            "${ContactEntry.COLUMN_NOM} COLLATE NOCASE ASC, ${ContactEntry.COLUMN_PRENOM} COLLATE NOCASE ASC"
        )
        return cursor.use { extractContactsFromCursor(it) }
    }

    private fun extractContactsFromCursor(cursor: Cursor): List<Contact> {
        val contacts = mutableListOf<Contact>()
        while (cursor.moveToNext()) {
            contacts.add(extractContactFromCursor(cursor))
        }
        return contacts
    }

    private fun extractContactFromCursor(cursor: Cursor): Contact {
        val idIndex = cursor.getColumnIndexOrThrow(ContactEntry.COLUMN_ID)
        val nomIndex = cursor.getColumnIndexOrThrow(ContactEntry.COLUMN_NOM)
        val prenomIndex = cursor.getColumnIndexOrThrow(ContactEntry.COLUMN_PRENOM)
        val phoneIndex = cursor.getColumnIndexOrThrow(ContactEntry.COLUMN_TELEPHONE)
        val emailIndex = cursor.getColumnIndexOrThrow(ContactEntry.COLUMN_EMAIL)
        val photoIndex = cursor.getColumnIndexOrThrow(ContactEntry.COLUMN_PHOTO_URI)
        val latIndex = cursor.getColumnIndexOrThrow(ContactEntry.COLUMN_LATITUDE)
        val lngIndex = cursor.getColumnIndexOrThrow(ContactEntry.COLUMN_LONGITUDE)

        val id = cursor.getLong(idIndex)
        val nom = cursor.getString(nomIndex)
        val prenom = cursor.getString(prenomIndex)
        val phone = cursor.getString(phoneIndex)
        val email = if (!cursor.isNull(emailIndex)) cursor.getString(emailIndex) else null
        val photoUri = if (!cursor.isNull(photoIndex)) cursor.getString(photoIndex) else null
        val lat = if (!cursor.isNull(latIndex)) cursor.getDouble(latIndex) else null
        val lng = if (!cursor.isNull(lngIndex)) cursor.getDouble(lngIndex) else null

        return Contact(id, nom, prenom, phone, email, photoUri, lat, lng)
    }
}
