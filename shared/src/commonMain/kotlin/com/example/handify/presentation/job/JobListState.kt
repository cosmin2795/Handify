package com.example.handify.presentation.job

import com.example.handify.domain.model.Job
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

data class JobListState(
    val jobs: List<Job> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedCategory: String = "all",
    val selectedSort: String = "recent",
    val userLat: Double? = null,
    val userLng: Double? = null,
    val currentUserId: String? = null
) {
    val filteredJobs: List<Job>
        get() {
            val ownFiltered = if (currentUserId != null) jobs.filter { it.clientId != currentUserId } else jobs
            val categoryFiltered = if (selectedCategory == "all") ownFiltered
            else ownFiltered.filter { it.category.name.lowercase() == selectedCategory }

            return when (selectedSort) {
                "budgetUp" -> categoryFiltered.sortedBy { it.budgetMin }
                "budgetDown" -> categoryFiltered.sortedByDescending { it.budgetMin }
                "near" -> {
                    val uLat = userLat
                    val uLng = userLng
                    if (uLat != null && uLng != null)
                        categoryFiltered.sortedBy { job ->
                            val jLat = job.lat
                            val jLng = job.lng
                            if (jLat != null && jLng != null) haversine(uLat, uLng, jLat, jLng)
                            else Double.MAX_VALUE
                        }
                    else categoryFiltered
                }
                else -> categoryFiltered.sortedByDescending { it.createdAt }
            }
        }
}

private fun haversine(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
    val r = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLng = Math.toRadians(lng2 - lng1)
    val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLng / 2).pow(2)
    return r * 2 * atan2(sqrt(a), sqrt(1 - a))
}
