package com.android_assignments.assignment_2_parveen.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Desk(
    val id: String,
    val displayName: String,
    val phone: String,
    val open: Boolean,
    val handledItems: Int,
    val rating: Float
) : Parcelable
