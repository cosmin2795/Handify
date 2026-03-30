package com.example.handify.features.job.api

import com.example.handify.features.job.domain.Job
import com.example.handify.features.job.domain.JobRepository
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.jobRoutes() {
    val jobRepository: JobRepository by inject()

    authenticate("jwt") {
        route("/jobs") {
            get {
                call.respond(jobRepository.getJobs().map { it.toResponse() })
            }
            get("/mine") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asString()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized)
                call.respond(jobRepository.getMyJobs(userId).map { it.toResponse() })
            }
        }
    }
}

private fun Job.toResponse() = JobResponse(
    id = id,
    title = title,
    description = description,
    category = category,
    location = location,
    budgetMin = budgetMin,
    budgetMax = budgetMax,
    duration = duration,
    status = status,
    isUrgent = isUrgent,
    clientId = clientId,
    clientName = clientName,
    clientRating = clientRating,
    applicantsCount = applicantsCount,
    createdAt = createdAt
)
