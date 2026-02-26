import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.bundling.Tar
import org.gradle.api.tasks.bundling.Zip

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinSerialization)
    application
}

tasks.named<Tar>("distTar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.named<Zip>("distZip") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

group = "com.example.handify"
version = "1.0.0"
application {
    mainClass.set("com.example.handify.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(projects.shared)

    // Ktor Server
    implementation(libs.logback)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.serialization.kotlinx.json.jvm)

    // Ktor Client (for calling Google verification APIs)
    implementation(libs.ktor.client.cio.jvm)
    implementation(libs.ktor.client.content.negotiation.jvm)

    // Database
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.java.time)
    implementation(libs.postgresql)

    // JWT
    implementation(libs.java.jwt)

    // DI
    implementation(libs.koin.ktor)
    implementation(libs.koin.core)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Test
    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.kotlin.testJunit)
}
