package com.example.handify.features.job.data

import com.example.handify.features.auth.data.UserTable
import org.jetbrains.exposed.sql.Table

object JobTable : Table("jobs") {
    val id = varchar("id", 36)
    val title = varchar("title", 255)
    val description = text("description")
    val category = varchar("category", 50)
    val location = varchar("location", 255)
    val budgetMin = double("budget_min")
    val budgetMax = double("budget_max")
    val duration = varchar("duration", 100)
    val status = varchar("status", 50)
    val isUrgent = bool("is_urgent")
    val clientId = varchar("client_id", 36).references(UserTable.id)
    val lat = double("lat").nullable()
    val lng = double("lng").nullable()
    val createdAt = long("created_at")

    override val primaryKey = PrimaryKey(id)
}
