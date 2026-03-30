package com.example.handify.data.remote.api

import com.example.handify.data.remote.response.JobResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class JobApi(private val client: HttpClient, private val baseUrl: String) {

    suspend fun getJobs(): List<JobResponse> =
        client.get("$baseUrl/api/jobs").body()

    suspend fun getMyJobs(): List<JobResponse> =
        client.get("$baseUrl/api/jobs/mine").body()
}
