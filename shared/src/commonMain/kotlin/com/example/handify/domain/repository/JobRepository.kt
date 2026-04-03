package com.example.handify.domain.repository

import com.example.handify.domain.model.Job
import com.example.handify.domain.model.JobCategory
import com.example.handify.domain.model.JobStatus

interface JobRepository {
    suspend fun getJobs(): List<Job>
    suspend fun getMyJobs(): List<Job>
    suspend fun createJob(
        title: String,
        description: String,
        category: JobCategory,
        location: String,
        budgetMin: Double,
        budgetMax: Double,
        duration: String,
        isUrgent: Boolean,
        status: JobStatus,
        lat: Double?,
        lng: Double?
    ): Job
}
