package com.example.handify.presentation.job

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.handify.domain.model.JobCategory
import com.example.handify.domain.model.JobStatus
import com.example.handify.domain.repository.JobRepository
import kotlinx.coroutines.launch

class PostJobViewModel(private val jobRepository: JobRepository) : ViewModel() {

    var state by mutableStateOf(PostJobState())
        private set

    fun reset() { state = PostJobState() }

    fun updateTitle(value: String) { state = state.copy(title = value) }
    fun updateCategory(value: JobCategory) { state = state.copy(category = value) }
    fun updateUrgency(value: Boolean) { state = state.copy(isUrgent = value) }
    fun updateDescription(value: String) { state = state.copy(description = value) }
    fun updateLocation(address: String, lat: Double, lng: Double) {
        state = state.copy(location = address, lat = lat, lng = lng)
    }
    fun updateDuration(value: String) { state = state.copy(duration = value) }
    fun updateBudget(value: String) { state = state.copy(budget = value) }

    fun nextStep() { if (state.step < 3) state = state.copy(step = state.step + 1) }
    fun prevStep() { if (state.step > 1) state = state.copy(step = state.step - 1) }

    fun publish() = submit(JobStatus.ACTIVE)
    fun saveDraft() = submit(JobStatus.DRAFT)

    private fun submit(status: JobStatus) {
        val s = state
        val category = s.category ?: return
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            try {
                val budget = s.budget.toDoubleOrNull() ?: 0.0
                jobRepository.createJob(
                    title = s.title,
                    description = s.description,
                    category = category,
                    location = s.location,
                    budgetMin = budget,
                    budgetMax = budget,
                    duration = s.duration,
                    isUrgent = s.isUrgent,
                    status = status,
                    lat = s.lat,
                    lng = s.lng
                )
                state = state.copy(isLoading = false, isSuccess = true, isDraft = status == JobStatus.DRAFT)
            } catch (e: Exception) {
                state = state.copy(isLoading = false, error = e.message ?: "Failed to post job")
            }
        }
    }
}
