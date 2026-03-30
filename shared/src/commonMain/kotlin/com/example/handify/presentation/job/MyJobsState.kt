package com.example.handify.presentation.job

import com.example.handify.domain.model.Job
import com.example.handify.domain.model.JobStatus

data class MyJobsState(
    val jobs: List<Job> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedStatus: JobStatus = JobStatus.ACTIVE
) {
    val filteredJobs: List<Job>
        get() = jobs.filter { it.status == selectedStatus }
}
