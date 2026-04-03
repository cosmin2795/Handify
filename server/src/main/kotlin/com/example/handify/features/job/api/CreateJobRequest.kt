package com.example.handify.features.job.api

import kotlinx.serialization.Serializable

@Serializable
data class CreateJobRequest(
    val title: String,
    val description: String,
    val category: String,
    val location: String,
    val budgetMin: Double,
    val budgetMax: Double,
    val duration: String,
    val isUrgent: Boolean,
    val status: String,
    val lat: Double? = null,
    val lng: Double? = null
)
