package com.android_assignments.assignment_2_parveen.data.repo

import com.android_assignments.assignment_2_parveen.data.models.SlipSettings
import kotlinx.coroutines.flow.Flow

interface SlipSettingsRepository {
    fun settingsFlow(): Flow<SlipSettings>
    suspend fun getSettings(): SlipSettings
    suspend fun saveSettings(settings: SlipSettings)
}
