package com.example.handify.core.database

import com.example.handify.features.auth.data.UserTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        // TODO: Move credentials to environment variables or application.conf
        val url = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/handify"
        val user = System.getenv("DB_USER") ?: "postgres"
        val password = System.getenv("DB_PASSWORD") ?: "postgres"

        Database.connect(
            url = url,
            driver = "org.postgresql.Driver",
            user = user,
            password = password
        )

        transaction {
            SchemaUtils.createMissingTablesAndColumns(UserTable)
        }
    }
}
