package com.example.handify.features.job.domain

import com.example.handify.features.job.api.CreateJobRequest

interface JobRepository {
    suspend fun getJobs(): List<Job>
    suspend fun getMyJobs(clientId: String): List<Job>
    suspend fun createJob(req: CreateJobRequest, clientId: String): Job
}
