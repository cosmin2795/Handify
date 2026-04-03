package com.example.handify.presentation.location

import com.example.handify.domain.model.SavedAddress

data class LocationState(
    val addresses: List<SavedAddress> = emptyList(),
    val activeAddressId: String? = null,
    val searchQuery: String = "",
    val showModal: Boolean = false,
    val showAddForm: Boolean = false,
    val newAddressText: String = "",
    val newAddressLabel: String = "Home",
    val editingId: String? = null
) {
    val activeAddress: SavedAddress? get() = addresses.find { it.id == activeAddressId }
    val displayCity: String
        get() = activeAddress?.fullAddress?.split(",")?.firstOrNull()?.trim() ?: "Location"
    val filteredAddresses: List<SavedAddress>
        get() = if (searchQuery.isBlank()) addresses
                else addresses.filter {
                    it.fullAddress.contains(searchQuery, ignoreCase = true) ||
                    it.label.contains(searchQuery, ignoreCase = true)
                }
}
