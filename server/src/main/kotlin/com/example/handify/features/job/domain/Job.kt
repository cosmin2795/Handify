package com.example.handify.features.job.domain

data class Job(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val location: String,
    val budgetMin: Double,
    val budgetMax: Double,
    val duration: String,
    val status: String,
    val isUrgent: Boolean,
    val clientId: String,
    val clientName: String,
    val clientRating: Double,
    val applicantsCount: Int,
    val lat: Double?,
    val lng: Double?,
    val createdAt: Long
)
