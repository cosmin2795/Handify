package com.example.handify.domain.model

data class Job(
    val id: String,
    val title: String,
    val description: String,
    val category: JobCategory,
    val location: String,
    val budgetMin: Double,
    val budgetMax: Double,
    val duration: String,
    val status: JobStatus,
    val isUrgent: Boolean,
    val clientId: String,
    val clientName: String,
    val clientRating: Double,
    val applicantsCount: Int,
    val lat: Double?,
    val lng: Double?,
    val createdAt: Long
)

enum class JobCategory { TRADES, CLEANING, MOVING, GARDEN, EVENTS, REPAIRS, TRANSPORT }

enum class JobStatus { ACTIVE, DRAFT, COMPLETED }
