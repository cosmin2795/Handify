package com.example.handify.presentation.job

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.handify.domain.repository.JobRepository
import kotlinx.coroutines.launch

class JobListViewModel(private val jobRepository: JobRepository) : ViewModel() {

    var state by mutableStateOf(JobListState())
        private set

    init {
        loadJobs()
    }

    fun loadJobs() {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            try {
                val jobs = jobRepository.getJobs()
                state = state.copy(jobs = jobs, isLoading = false)
            } catch (e: Exception) {
                state = state.copy(isLoading = false, error = e.message ?: "Failed to load jobs")
            }
        }
    }

    fun selectCategory(category: String) {
        state = state.copy(selectedCategory = category)
    }

    fun selectSort(sort: String) {
        state = state.copy(selectedSort = sort)
    }
}
