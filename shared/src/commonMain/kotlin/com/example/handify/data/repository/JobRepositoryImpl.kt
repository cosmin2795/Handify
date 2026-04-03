package com.example.handify.data.repository

import com.example.handify.data.remote.api.JobApi
import com.example.handify.data.remote.mapper.toDomain
import com.example.handify.data.remote.request.CreateJobRequest
import com.example.handify.domain.model.Job
import com.example.handify.domain.model.JobCategory
import com.example.handify.domain.model.JobStatus
import com.example.handify.domain.repository.JobRepository

class JobRepositoryImpl(private val jobApi: JobApi) : JobRepository {

    override suspend fun getJobs(): List<Job> =
        jobApi.getJobs().map { it.toDomain() }

    override suspend fun getMyJobs(): List<Job> =
        jobApi.getMyJobs().map { it.toDomain() }

    override suspend fun createJob(
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
    ): Job = jobApi.createJob(
        CreateJobRequest(
            title = title,
            description = description,
            category = category.name,
            location = location,
            budgetMin = budgetMin,
            budgetMax = budgetMax,
            duration = duration,
            isUrgent = isUrgent,
            status = status.name,
            lat = lat,
            lng = lng
        )
    ).toDomain()
}
