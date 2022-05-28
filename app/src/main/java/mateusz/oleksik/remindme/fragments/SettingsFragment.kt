package mateusz.oleksik.remindme.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import mateusz.oleksik.remindme.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}