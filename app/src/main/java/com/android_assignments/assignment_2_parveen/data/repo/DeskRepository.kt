package com.android_assignments.assignment_2_parveen.data.repo

import com.android_assignments.assignment_2_parveen.data.models.Desk

interface DeskRepository {
    fun getDesks(): List<Desk>
}
