package com.example.liste_de_contacts_v2.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.liste_de_contacts_v2.data.ContactContract.ContactEntry

/**
 * Gestionnaire de base de données SQLite pour l'application Contacts (TP V4).
 * Pattern Singleton basé sur applicationContext.
 */
class ContactDbHelper private constructor(context: Context) : SQLiteOpenHelper(
    context.applicationContext,
    ContactContract.DATABASE_NAME,
    null,
    ContactContract.DATABASE_VERSION
) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTableSql = """
            CREATE TABLE ${ContactEntry.TABLE_NAME} (
                ${ContactEntry.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${ContactEntry.COLUMN_NOM} TEXT NOT NULL,
                ${ContactEntry.COLUMN_PRENOM} TEXT NOT NULL,
                ${ContactEntry.COLUMN_TELEPHONE} TEXT NOT NULL,
                ${ContactEntry.COLUMN_EMAIL} TEXT,
                ${ContactEntry.COLUMN_PHOTO_URI} TEXT,
                ${ContactEntry.COLUMN_LATITUDE} REAL,
                ${ContactEntry.COLUMN_LONGITUDE} REAL
            );
        """.trimIndent()

        db.execSQL(createTableSql)
        insertDemoData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${ContactEntry.TABLE_NAME}")
        onCreate(db)
    }

    private fun insertDemoData(db: SQLiteDatabase) {
        val demoContacts = listOf(
            arrayOf("Dupont", "Claire", "06 12 34 56 78", "claire.dupont@example.com", null, "48.8566", "2.3522"),
            arrayOf("Martin", "Thomas", "07 23 45 67 89", "thomas.martin@example.com", null, "45.7640", "4.8357"),
            arrayOf("Bernard", "Sophie", "06 98 76 54 32", "sophie.bernard@example.com", null, null, null), // Sans GPS
            arrayOf("Petit", "Lucas", "07 11 22 33 44", "lucas.petit@example.com", null, "43.2965", "5.3698"),
            arrayOf("Moreau", "Julien", "06 55 44 33 22", "julien.moreau@example.com", null, null, null)  // Sans GPS
        )

        for (contact in demoContacts) {
            val values = ContentValues().apply {
                put(ContactEntry.COLUMN_NOM, contact[0])
                put(ContactEntry.COLUMN_PRENOM, contact[1])
                put(ContactEntry.COLUMN_TELEPHONE, contact[2])
                put(ContactEntry.COLUMN_EMAIL, contact[3])
                put(ContactEntry.COLUMN_PHOTO_URI, contact[4])
                if (contact[5] != null) put(ContactEntry.COLUMN_LATITUDE, contact[5]!!.toDouble()) else putNull(ContactEntry.COLUMN_LATITUDE)
                if (contact[6] != null) put(ContactEntry.COLUMN_LONGITUDE, contact[6]!!.toDouble()) else putNull(ContactEntry.COLUMN_LONGITUDE)
            }
            db.insert(ContactEntry.TABLE_NAME, null, values)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ContactDbHelper? = null

        fun getInstance(context: Context): ContactDbHelper {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ContactDbHelper(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
