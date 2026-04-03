package com.example.handify.domain.source

data class UserLocation(val lat: Double, val lng: Double)

interface LocationSource {
    suspend fun getCurrentLocation(): UserLocation?
}
