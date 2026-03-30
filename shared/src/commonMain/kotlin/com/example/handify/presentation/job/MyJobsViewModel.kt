package com.example.handify.presentation.job

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.handify.domain.model.JobStatus
import com.example.handify.domain.repository.JobRepository
import kotlinx.coroutines.launch

class MyJobsViewModel(private val jobRepository: JobRepository) : ViewModel() {

    var state by mutableStateOf(MyJobsState())
        private set

    init {
        loadMyJobs()
    }

    fun loadMyJobs() {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            try {
                val jobs = jobRepository.getMyJobs()
                state = state.copy(jobs = jobs, isLoading = false)
            } catch (e: Exception) {
                state = state.copy(isLoading = false, error = e.message ?: "Failed to load jobs")
            }
        }
    }

    fun selectStatus(status: JobStatus) {
        state = state.copy(selectedStatus = status)
    }
}
