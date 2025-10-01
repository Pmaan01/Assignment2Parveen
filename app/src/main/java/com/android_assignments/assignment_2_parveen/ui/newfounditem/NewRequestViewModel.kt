package com.android_assignments.assignment_2_parveen.ui.newfounditem

import androidx.lifecycle.*
import com.android_assignments.assignment_2_parveen.data.models.FoundItem
import com.android_assignments.assignment_2_parveen.data.models.SlipSettings
import com.android_assignments.assignment_2_parveen.data.repo.FoundItemRepository
import com.android_assignments.assignment_2_parveen.data.repo.SlipSettingsRepository
import com.android_assignments.assignment_2_parveen.util.Event
import kotlinx.coroutines.launch
import java.util.*

data class ValidationResult(val nameError: String? = null, val locationError: String? = null) {
    val isValid get() = nameError == null && locationError == null
}

data class ReviewNavigationPayload(val foundItem: FoundItem, val settings: SlipSettings)

class NewFoundItemViewModel(
    private val foundItemRepo: FoundItemRepository,
    private val settingsRepo: SlipSettingsRepository
) : ViewModel() {

    val name = MutableLiveData("")
    val foundLocation = MutableLiveData("")
    val description = MutableLiveData<String?>("")
    val notes = MutableLiveData<String?>("")
    val photoUriString = MutableLiveData<String?>(null)

    private val _validation = MutableLiveData<ValidationResult>()
    val validation: LiveData<ValidationResult> = _validation

    private val _navigateToReview = MutableLiveData<Event<ReviewNavigationPayload>>()
    val navigateToReview: LiveData<Event<ReviewNavigationPayload>> = _navigateToReview

    fun setPhotoUri(uriString: String?) {
        photoUriString.value = uriString
    }

    private fun validateInputs(): ValidationResult {
        val n = name.value?.trim().orEmpty()
        val loc = foundLocation.value?.trim().orEmpty()
        val nameErr = if (n.isEmpty()) "Name required" else null
        val locErr = if (loc.isEmpty()) "Location required" else null
        val result = ValidationResult(nameErr, locErr)
        _validation.value = result
        return result
    }

    fun onReviewRequested(includePhotoInSettings: Boolean = true) {
        val v = validateInputs()
        if (!v.isValid) return

        val item = FoundItem(
            id = UUID.randomUUID().toString(),
            name = name.value!!.trim(),
            description = description.value?.trim().takeIf { !it.isNullOrEmpty() },
            foundLocation = foundLocation.value!!.trim(),
            notes = notes.value?.trim().takeIf { !it.isNullOrEmpty() },
            photoUri = photoUriString.value,
            createdAt = System.currentTimeMillis()
        )

        viewModelScope.launch {
            foundItemRepo.add(item)
            val settings = settingsRepo.getSettings().copy(includePhoto = includePhotoInSettings)
            _navigateToReview.value = Event(ReviewNavigationPayload(item, settings))
        }
    }

    class Factory(
        private val foundItemRepo: FoundItemRepository,
        private val settingsRepo: SlipSettingsRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NewFoundItemViewModel::class.java)) {
                return NewFoundItemViewModel(foundItemRepo, settingsRepo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
