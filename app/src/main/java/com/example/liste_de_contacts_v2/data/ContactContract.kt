package com.example.liste_de_contacts_v2.data

import android.provider.BaseColumns

/**
 * Contrat SQLite définissant la structure de la table 'contacts'.
 * Regroupe le nom de la base, de la table et des colonnes en constantes.
 */
object ContactContract {
    const val DATABASE_NAME = "contacts.db"
    const val DATABASE_VERSION = 1

    object ContactEntry : BaseColumns {
        const val TABLE_NAME = "contacts"
        const val COLUMN_ID = BaseColumns._ID // "_id"
        const val COLUMN_NOM = "nom"
        const val COLUMN_PRENOM = "prenom"
        const val COLUMN_TELEPHONE = "telephone"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PHOTO_URI = "photo_uri"
        const val COLUMN_LATITUDE = "latitude"
        const val COLUMN_LONGITUDE = "longitude"
    }
}
