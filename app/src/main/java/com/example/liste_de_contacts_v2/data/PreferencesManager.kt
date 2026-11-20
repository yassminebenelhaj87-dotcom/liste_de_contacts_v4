package com.example.liste_de_contacts_v2.data

import android.content.Context
import android.graphics.Color
import androidx.preference.PreferenceManager

/**
 * Gestionnaire centralisé pour la lecture et la conversion des SharedPreferences.
 * Fournit des valeurs directement utilisables par les vues et adapters (Float sp, Int Color, Boolean).
 */
class PreferencesManager(context: Context) {

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)

    val fontSizeSp: Float
        get() {
            val sizeStr = prefs.getString("taille_police", "16") ?: "16"
            return sizeStr.toFloatOrNull() ?: 16f
        }

    val textColor: Int
        get() {
            val colorStr = prefs.getString("couleur_preferee", "#000000") ?: "#000000"
            return try {
                Color.parseColor(colorStr)
            } catch (_: Exception) {
                Color.BLACK
            }
        }

    val isCallFromListEnabled: Boolean
        get() = prefs.getBoolean("appel_depuis_liste", false)
}
