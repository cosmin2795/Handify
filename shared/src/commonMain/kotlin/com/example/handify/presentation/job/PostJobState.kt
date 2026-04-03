package com.example.handify.presentation.job

import com.example.handify.domain.model.JobCategory

data class PostJobState(
    val step: Int = 1,
    val title: String = "",
    val category: JobCategory? = null,
    val isUrgent: Boolean = false,
    val description: String = "",
    val location: String = "",
    val lat: Double? = null,
    val lng: Double? = null,
    val duration: String = "",
    val budget: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isDraft: Boolean = false,
    val error: String? = null
)
