package com.example.handify.presentation.job

import com.example.handify.domain.model.Job

data class JobListState(
    val jobs: List<Job> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedCategory: String = "all",
    val selectedSort: String = "recent"
) {
    val filteredJobs: List<Job>
        get() {
            val categoryFiltered = if (selectedCategory == "all") jobs
            else jobs.filter { it.category.name.lowercase() == selectedCategory }

            return when (selectedSort) {
                "budgetUp" -> categoryFiltered.sortedBy { it.budgetMin }
                "budgetDown" -> categoryFiltered.sortedByDescending { it.budgetMin }
                "near" -> categoryFiltered
                else -> categoryFiltered.sortedByDescending { it.createdAt }
            }
        }
}
