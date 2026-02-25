package com.example.handify.features.auth.data

import com.example.handify.core.database.dbQuery
import com.example.handify.features.auth.domain.AuthProvider
import com.example.handify.features.auth.domain.User
import com.example.handify.features.auth.domain.UserRepository
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import java.util.UUID

class UserRepositoryImpl : UserRepository {

    override suspend fun findById(id: String): User? = dbQuery {
        UserTable.selectAll()
            .where { UserTable.id eq id }
            .singleOrNull()
            ?.toUser()
    }

    override suspend fun findByEmail(email: String): User? = dbQuery {
        UserTable.selectAll()
            .where { UserTable.email eq email }
            .singleOrNull()
            ?.toUser()
    }

    override suspend fun findByProviderId(providerId: String, provider: AuthProvider): User? = dbQuery {
        UserTable.selectAll()
            .where { (UserTable.providerId eq providerId) and (UserTable.provider eq provider.name) }
            .singleOrNull()
            ?.toUser()
    }

    override suspend fun createUser(
        email: String,
        name: String,
        providerId: String,
        provider: AuthProvider
    ): User = dbQuery {
        val id = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        UserTable.insert {
            it[UserTable.id] = id
            it[UserTable.email] = email
            it[UserTable.name] = name
            it[UserTable.providerId] = providerId
            it[UserTable.provider] = provider.name
            it[createdAt] = now
        }
        User(id = id, email = email, name = name, providerId = providerId, provider = provider, createdAt = now)
    }

    private fun ResultRow.toUser() = User(
        id = this[UserTable.id],
        email = this[UserTable.email],
        name = this[UserTable.name],
        providerId = this[UserTable.providerId],
        provider = AuthProvider.valueOf(this[UserTable.provider]),
        createdAt = this[UserTable.createdAt]
    )
}
