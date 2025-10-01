package com.android_assignments.assignment_2_parveen.data.repo

import android.content.Context

object DataRepoModule {
    fun provideFoundItemRepo(): FoundItemRepository = InMemoryFoundItemRepository()
    fun provideDeskRepo(): DeskRepository = StaticDeskRepository()
    fun provideSlipSettingsRepo(context: Context): SlipSettingsRepository =
        SharedPrefsSlipSettingsRepository(context)
}
