package com.example.handify.data.source

import android.annotation.SuppressLint
import android.content.Context
import com.example.handify.domain.source.LocationSource
import com.example.handify.domain.source.UserLocation
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await

class AndroidLocationSource(private val context: Context) : LocationSource {

    private val client = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): UserLocation? {
        return try {
            val cts = CancellationTokenSource()
            val location = client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token).await()
            location?.let { UserLocation(it.latitude, it.longitude) }
        } catch (e: Exception) {
            null
        }
    }
}
