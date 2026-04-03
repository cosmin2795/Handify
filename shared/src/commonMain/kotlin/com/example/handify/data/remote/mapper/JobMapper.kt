package com.example.handify.data.remote.mapper

import com.example.handify.data.remote.response.JobResponse
import com.example.handify.domain.model.Job
import com.example.handify.domain.model.JobCategory
import com.example.handify.domain.model.JobStatus

fun JobResponse.toDomain(): Job = Job(
    id = id,
    title = title,
    description = description,
    category = runCatching { JobCategory.valueOf(category) }.getOrDefault(JobCategory.TRADES),
    location = location,
    budgetMin = budgetMin,
    budgetMax = budgetMax,
    duration = duration,
    status = runCatching { JobStatus.valueOf(status) }.getOrDefault(JobStatus.ACTIVE),
    isUrgent = isUrgent,
    clientId = clientId,
    clientName = clientName,
    clientRating = clientRating,
    applicantsCount = applicantsCount,
    lat = lat,
    lng = lng,
    createdAt = createdAt
)
