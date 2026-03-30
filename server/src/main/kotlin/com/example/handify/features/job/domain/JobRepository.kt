package com.example.handify.features.job.domain

interface JobRepository {
    suspend fun getJobs(): List<Job>
}
