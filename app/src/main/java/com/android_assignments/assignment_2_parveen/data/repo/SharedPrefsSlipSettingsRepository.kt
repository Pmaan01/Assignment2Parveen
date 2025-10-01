package com.android_assignments.assignment_2_parveen.data.repo

import android.content.Context
import com.android_assignments.assignment_2_parveen.data.models.SlipSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class SharedPrefsSlipSettingsRepository(context: Context) : SlipSettingsRepository {
    private val prefs = context.getSharedPreferences("slip_settings", Context.MODE_PRIVATE)
    private val _state = MutableStateFlow(loadFromPrefs())

    override fun settingsFlow(): Flow<SlipSettings> = _state

    private fun loadFromPrefs(): SlipSettings {
        val includePhoto = prefs.getBoolean("include_photo", true)
        val campusCode = prefs.getString("campus_code", "MAIN") ?: "MAIN"
        return SlipSettings(includePhoto = includePhoto, campusCode = campusCode)
    }

    override suspend fun getSettings(): SlipSettings = _state.value

    override suspend fun saveSettings(settings: SlipSettings) {
        prefs.edit()
            .putBoolean("include_photo", settings.includePhoto)
            .putString("campus_code", settings.campusCode)
            .apply()
        _state.update { settings }
    }
}
