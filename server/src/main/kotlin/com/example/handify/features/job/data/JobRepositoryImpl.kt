package com.example.handify.features.job.data

import com.example.handify.core.database.dbQuery
import com.example.handify.features.auth.data.UserTable
import com.example.handify.features.job.api.CreateJobRequest
import com.example.handify.features.job.domain.Job
import com.example.handify.features.job.domain.JobRepository
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import java.util.UUID

class JobRepositoryImpl : JobRepository {

    override suspend fun getJobs(): List<Job> = dbQuery {
        JobTable.join(UserTable, org.jetbrains.exposed.sql.JoinType.LEFT, JobTable.clientId, UserTable.id)
            .selectAll()
            .where { JobTable.status eq "ACTIVE" }
            .map { it.toJob() }
    }

    override suspend fun getMyJobs(clientId: String): List<Job> = dbQuery {
        JobTable.join(UserTable, org.jetbrains.exposed.sql.JoinType.LEFT, JobTable.clientId, UserTable.id)
            .selectAll()
            .where { JobTable.clientId eq clientId }
            .map { it.toJob() }
    }

    override suspend fun createJob(req: CreateJobRequest, clientId: String): Job = dbQuery {
        val id = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        JobTable.insert {
            it[JobTable.id] = id
            it[JobTable.title] = req.title
            it[JobTable.description] = req.description
            it[JobTable.category] = req.category
            it[JobTable.location] = req.location
            it[JobTable.budgetMin] = req.budgetMin
            it[JobTable.budgetMax] = req.budgetMax
            it[JobTable.duration] = req.duration
            it[JobTable.status] = req.status
            it[JobTable.isUrgent] = req.isUrgent
            it[JobTable.clientId] = clientId
            it[JobTable.lat] = req.lat
            it[JobTable.lng] = req.lng
            it[JobTable.createdAt] = now
        }
        val clientName = UserTable.selectAll().where { UserTable.id eq clientId }
            .firstOrNull()?.get(UserTable.name) ?: "Unknown"
        Job(
            id = id, title = req.title, description = req.description,
            category = req.category, location = req.location,
            budgetMin = req.budgetMin, budgetMax = req.budgetMax,
            duration = req.duration, status = req.status, isUrgent = req.isUrgent,
            clientId = clientId, clientName = clientName,
            clientRating = 0.0, applicantsCount = 0,
            lat = req.lat, lng = req.lng, createdAt = now
        )
    }

    private fun org.jetbrains.exposed.sql.ResultRow.toJob() = Job(
        id = this[JobTable.id],
        title = this[JobTable.title],
        description = this[JobTable.description],
        category = this[JobTable.category],
        location = this[JobTable.location],
        budgetMin = this[JobTable.budgetMin],
        budgetMax = this[JobTable.budgetMax],
        duration = this[JobTable.duration],
        status = this[JobTable.status],
        isUrgent = this[JobTable.isUrgent],
        clientId = this[JobTable.clientId],
        clientName = this.getOrNull(UserTable.name) ?: "Unknown",
        clientRating = 0.0,
        applicantsCount = 0,
        lat = this[JobTable.lat],
        lng = this[JobTable.lng],
        createdAt = this[JobTable.createdAt]
    )
}
