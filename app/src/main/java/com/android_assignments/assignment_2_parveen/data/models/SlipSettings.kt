package com.android_assignments.assignment_2_parveen.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SlipSettings(
    val includePhoto: Boolean = true,
    val campusCode: String = "MAIN"
) : Parcelable
