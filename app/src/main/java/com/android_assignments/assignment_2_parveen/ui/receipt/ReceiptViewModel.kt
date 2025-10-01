package com.android_assignments.assignment_2_parveen.ui.receipt

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android_assignments.assignment_2_parveen.data.models.FoundItem
import com.android_assignments.assignment_2_parveen.data.models.PickupSlip
import com.android_assignments.assignment_2_parveen.data.models.SlipSettings
import com.android_assignments.assignment_2_parveen.data.repo.FoundItemRepository
import com.android_assignments.assignment_2_parveen.util.Event

class ReceiptViewModel(private val foundItemRepo: FoundItemRepository) : ViewModel() {

    private val _shareIntentEvent = MutableLiveData<Event<Intent>>()
    val shareIntentEvent: LiveData<Event<Intent>> = _shareIntentEvent

    fun createShareIntent(foundItem: FoundItem, slip: PickupSlip, settings: SlipSettings): Intent {
        val text = slip.summary
        return if (settings.includePhoto && !foundItem.photoUri.isNullOrEmpty()) {
            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, text)
                putExtra(Intent.EXTRA_STREAM, Uri.parse(foundItem.photoUri))
                type = "image/*"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        } else {
            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, text)
                type = "text/plain"
            }
        }
    }

    fun requestShare(foundItem: FoundItem, slip: PickupSlip, settings: SlipSettings) {
        val intent = createShareIntent(foundItem, slip, settings)
        _shareIntentEvent.value = Event(intent)
    }

    class Factory(private val foundItemRepo: FoundItemRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ReceiptViewModel::class.java)) {
                return ReceiptViewModel(foundItemRepo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
