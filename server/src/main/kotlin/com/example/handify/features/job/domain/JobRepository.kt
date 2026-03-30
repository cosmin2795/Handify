package com.example.handify.features.job.domain

interface JobRepository {
    suspend fun getJobs(): List<Job>
    suspend fun getMyJobs(clientId: String): List<Job>
}
