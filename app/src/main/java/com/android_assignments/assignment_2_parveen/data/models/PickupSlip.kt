package com.android_assignments.assignment_2_parveen.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PickupSlip(
    val receiptId: String,
    val foundItemId: String,
    val deskId: String,
    val issuedAt: Long,
    val summary: String
) : Parcelable
