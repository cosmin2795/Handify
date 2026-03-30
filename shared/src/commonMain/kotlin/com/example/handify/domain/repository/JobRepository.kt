package com.example.handify.domain.repository

import com.example.handify.domain.model.Job

interface JobRepository {
    suspend fun getJobs(): List<Job>
}
