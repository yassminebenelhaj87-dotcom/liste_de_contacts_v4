package com.example.liste_de_contacts_v2

import java.io.Serializable

/**
 * Modèle de données représentant un Contact pour SQLite (TP V4).
 */
data class Contact(
    var id: Long = 0L,
    var nom: String,
    var prenom: String,
    var telephone: String,
    var email: String? = null,
    var photoUri: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null
) : Serializable {
    // Alias rétro-compatible si nécessaire
    var imageUri: String?
        get() = photoUri
        set(value) {
            photoUri = value
        }
}
