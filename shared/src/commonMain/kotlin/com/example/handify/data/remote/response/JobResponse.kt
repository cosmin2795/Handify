package com.example.handify.data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class JobResponse(
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
    val createdAt: Long
)
