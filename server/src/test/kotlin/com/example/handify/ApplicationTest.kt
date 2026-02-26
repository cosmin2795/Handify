package com.example.handify

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {

    @Test
    fun testRoot() = testApplication {
        application {
            module(initDatabase = false)
        }
        val response = client.get("/")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }
}
