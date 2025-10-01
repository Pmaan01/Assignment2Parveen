package com.android_assignments.assignment_2_parveen.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FoundItem(
    val id: String,
    val name: String,
    val description: String?,
    val foundLocation: String,
    val notes: String?,
    val photoUri: String?, // string from Uri.toString()
    val createdAt: Long
) : Parcelable
