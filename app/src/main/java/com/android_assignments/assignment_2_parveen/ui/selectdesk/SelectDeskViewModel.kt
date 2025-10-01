package com.android_assignments.assignment_2_parveen.ui.selectdesk

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.android_assignments.assignment_2_parveen.data.models.Desk
import com.android_assignments.assignment_2_parveen.data.repo.DataRepoModule
import com.android_assignments.assignment_2_parveen.data.repo.DeskRepository
import com.android_assignments.assignment_2_parveen.util.Event

class SelectDeskViewModel(
    private val deskRepo: DeskRepository = DataRepoModule.provideDeskRepo()
) : ViewModel() {

    // List of desks for the UI to render
    val desks: LiveData<List<Desk>> = liveData {
        emit(deskRepo.getDesks())
    }

    // One-shot event when a desk is chosen
    private val _deskSelectedEvent = MutableLiveData<Event<Desk>>()
    val deskSelectedEvent: LiveData<Event<Desk>> = _deskSelectedEvent

    // Call when user taps a desk card
    fun selectDesk(desk: Desk) {
        if (!desk.open) return
        _deskSelectedEvent.value = Event(desk)
    }

    class Factory(private val deskRepo: DeskRepository = DataRepoModule.provideDeskRepo()) :
        androidx.lifecycle.ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SelectDeskViewModel::class.java)) {
                return SelectDeskViewModel(deskRepo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
