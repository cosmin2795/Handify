package com.example.handify.features.job.data

import com.example.handify.core.database.dbQuery
import com.example.handify.features.auth.data.UserTable
import com.example.handify.features.job.domain.Job
import com.example.handify.features.job.domain.JobRepository
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll

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
        createdAt = this[JobTable.createdAt]
    )
}
