package com.example.handify.features.job.api

import com.example.handify.features.job.domain.JobRepository
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.jobRoutes() {
    val jobRepository: JobRepository by inject()

    authenticate("jwt") {
        route("/jobs") {
            get {
                val jobs = jobRepository.getJobs()
                call.respond(jobs.map { job ->
                    JobResponse(
                        id = job.id,
                        title = job.title,
                        description = job.description,
                        category = job.category,
                        location = job.location,
                        budgetMin = job.budgetMin,
                        budgetMax = job.budgetMax,
                        duration = job.duration,
                        status = job.status,
                        isUrgent = job.isUrgent,
                        clientId = job.clientId,
                        clientName = job.clientName,
                        clientRating = job.clientRating,
                        applicantsCount = job.applicantsCount,
                        createdAt = job.createdAt
                    )
                })
            }
        }
    }
}
