package com.android_assignments.assignment_2_parveen.ui.review

import androidx.lifecycle.*
import com.android_assignments.assignment_2_parveen.data.models.Desk
import com.android_assignments.assignment_2_parveen.data.models.FoundItem
import com.android_assignments.assignment_2_parveen.data.models.PickupSlip
import com.android_assignments.assignment_2_parveen.data.models.SlipSettings
import com.android_assignments.assignment_2_parveen.data.repo.DeskRepository
import com.android_assignments.assignment_2_parveen.util.Event
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class ReviewState(val selectedDesk: Desk? = null, val confirmVisible: Boolean = false)

class ReviewItemViewModel(private val deskRepo: DeskRepository) : ViewModel() {

    private val _state = MutableLiveData(ReviewState())
    val state: LiveData<ReviewState> = _state

    private val _navigateToReceipt = MutableLiveData<Event<PickupSlip>>()
    val navigateToReceipt: LiveData<Event<PickupSlip>> = _navigateToReceipt

    val desks: LiveData<List<Desk>> = liveData { emit(deskRepo.getDesks()) }

    fun setSelectedDesk(desk: Desk) {
        _state.value = ReviewState(selectedDesk = desk, confirmVisible = true)
    }

    fun buildAndEmitSlip(foundItem: FoundItem, settings: SlipSettings) {
        val desk = _state.value?.selectedDesk ?: return
        viewModelScope.launch {
            val issuedAt = System.currentTimeMillis()
            val dateText = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(issuedAt))
            val summary = "Pickup Slip: ${foundItem.name} → ${desk.displayName}, issued $dateText"
            val slip = PickupSlip(
                receiptId = UUID.randomUUID().toString(),
                foundItemId = foundItem.id,
                deskId = desk.id,
                issuedAt = issuedAt,
                summary = summary
            )
            _navigateToReceipt.value = Event(slip)
        }
    }

    class Factory(private val deskRepo: DeskRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ReviewItemViewModel::class.java)) {
                return ReviewItemViewModel(deskRepo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
