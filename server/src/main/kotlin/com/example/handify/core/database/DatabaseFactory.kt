package com.example.handify.core.database

import com.example.handify.features.auth.data.UserTable
import com.example.handify.features.job.data.JobTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        val url = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/handify"
        val user = System.getenv("DB_USER") ?: System.getProperty("user.name")
        val password = System.getenv("DB_PASSWORD") ?: ""

        Database.connect(
            url = url,
            driver = "org.postgresql.Driver",
            user = user,
            password = password
        )

        transaction {
            SchemaUtils.createMissingTablesAndColumns(UserTable, JobTable)
        }
    }
}
