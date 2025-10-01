package com.android_assignments.assignment_2_parveen.data.repo

import com.android_assignments.assignment_2_parveen.data.models.Desk

class StaticDeskRepository : DeskRepository {
    override fun getDesks(): List<Desk> = listOf(
        Desk("north", "Security North", "+1 (555) 100-0100", true, 320, 4.6f),
        Desk("south", "Security South", "+1 (555) 100-0200", true, 280, 4.5f),
        Desk("library", "Library Desk", "+1 (555) 200-0300", true, 410, 4.7f),
        Desk("housing", "Housing Office", "+1 (555) 300-0400", false, 150, 4.4f)
    )
}
