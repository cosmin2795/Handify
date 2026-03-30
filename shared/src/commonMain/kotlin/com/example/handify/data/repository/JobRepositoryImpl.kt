package com.example.handify.data.repository

import com.example.handify.data.remote.api.JobApi
import com.example.handify.data.remote.mapper.toDomain
import com.example.handify.domain.model.Job
import com.example.handify.domain.repository.JobRepository

class JobRepositoryImpl(private val jobApi: JobApi) : JobRepository {

    override suspend fun getJobs(): List<Job> =
        jobApi.getJobs().map { it.toDomain() }
}
