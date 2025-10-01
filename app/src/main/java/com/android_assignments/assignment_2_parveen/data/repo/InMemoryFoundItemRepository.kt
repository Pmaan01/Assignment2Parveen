package com.android_assignments.assignment_2_parveen.data.repo

import com.android_assignments.assignment_2_parveen.data.models.FoundItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InMemoryFoundItemRepository : FoundItemRepository {
    private val mutex = Mutex()
    private val _items = MutableStateFlow<List<FoundItem>>(emptyList())

    override fun getAll(): Flow<List<FoundItem>> = _items.asStateFlow()

    override suspend fun add(foundItem: FoundItem) {
        mutex.withLock {
            val new = _items.value.toMutableList().apply { add(0, foundItem) }
            _items.value = new
        }
    }

    override suspend fun getById(id: String): FoundItem? =
        _items.value.firstOrNull { it.id == id }

    override suspend fun clearAll() {
        _items.value = emptyList()
    }
}
