package com.example.handify.features.job.data

import com.example.handify.core.database.dbQuery
import com.example.handify.features.auth.data.UserTable
import com.example.handify.features.job.domain.Job
import com.example.handify.features.job.domain.JobRepository
import org.jetbrains.exposed.sql.selectAll

class JobRepositoryImpl : JobRepository {

    override suspend fun getJobs(): List<Job> = dbQuery {
        JobTable.join(UserTable, org.jetbrains.exposed.sql.JoinType.LEFT, JobTable.clientId, UserTable.id)
            .selectAll()
            .where { JobTable.status eq "ACTIVE" }
            .map { row ->
                Job(
                    id = row[JobTable.id],
                    title = row[JobTable.title],
                    description = row[JobTable.description],
                    category = row[JobTable.category],
                    location = row[JobTable.location],
                    budgetMin = row[JobTable.budgetMin],
                    budgetMax = row[JobTable.budgetMax],
                    duration = row[JobTable.duration],
                    status = row[JobTable.status],
                    isUrgent = row[JobTable.isUrgent],
                    clientId = row[JobTable.clientId],
                    clientName = row.getOrNull(UserTable.name) ?: "Unknown",
                    clientRating = 0.0,
                    applicantsCount = 0,
                    createdAt = row[JobTable.createdAt]
                )
            }
    }
}
