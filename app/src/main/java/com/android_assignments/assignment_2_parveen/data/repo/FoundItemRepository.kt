package com.android_assignments.assignment_2_parveen.data.repo

import com.android_assignments.assignment_2_parveen.data.models.FoundItem
import kotlinx.coroutines.flow.Flow

interface FoundItemRepository {
    suspend fun add(foundItem: FoundItem)
    suspend fun getById(id: String): FoundItem?
    fun getAll(): Flow<List<FoundItem>>
    suspend fun clearAll()
}
