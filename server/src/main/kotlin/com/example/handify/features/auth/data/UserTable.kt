package com.example.handify.features.auth.data

import org.jetbrains.exposed.sql.Table

object UserTable : Table("users") {
    val id = varchar("id", 36)
    val email = varchar("email", 255).uniqueIndex()
    val name = varchar("name", 255)
    val providerId = varchar("provider_id", 255)
    val provider = varchar("provider", 50)
    val createdAt = long("created_at")

    override val primaryKey = PrimaryKey(id)
}
