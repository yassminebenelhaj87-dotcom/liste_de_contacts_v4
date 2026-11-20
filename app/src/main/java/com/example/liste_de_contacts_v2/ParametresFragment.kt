package com.example.liste_de_contacts_v2

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

/**
 * Fragment gérant l'écran des préférences utilisateur (TP V4).
 * Utilise AndroidX Preference et la ressource res/xml/preferences.xml.
 */
class ParametresFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}
