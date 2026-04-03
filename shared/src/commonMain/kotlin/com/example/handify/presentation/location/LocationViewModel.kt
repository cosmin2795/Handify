package com.example.handify.presentation.location

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.handify.domain.model.SavedAddress
import com.example.handify.domain.source.AddressStorage

class LocationViewModel(private val addressStorage: AddressStorage) : ViewModel() {

    var state by mutableStateOf(LocationState())
        private set

    init {
        loadAddresses()
    }

    private fun loadAddresses() {
        val all = addressStorage.getAll()
        state = state.copy(addresses = all, activeAddressId = addressStorage.getActiveId())
    }

    fun selectAddress(id: String) {
        addressStorage.setActiveId(id)
        state = state.copy(activeAddressId = id, showModal = false, searchQuery = "")
    }

    fun removeAddress(id: String) {
        addressStorage.remove(id)
        if (state.activeAddressId == id) {
            val remaining = addressStorage.getAll()
            val newActive = remaining.firstOrNull()?.id
            addressStorage.setActiveId(newActive)
            state = state.copy(addresses = remaining, activeAddressId = newActive)
        } else {
            state = state.copy(addresses = addressStorage.getAll())
        }
    }

    fun openModal() {
        state = state.copy(showModal = true, searchQuery = "", showAddForm = false)
    }

    fun closeModal() {
        state = state.copy(showModal = false, searchQuery = "", showAddForm = false, newAddressText = "", newAddressLabel = "Home", editingId = null)
    }

    fun openAddForm(editingId: String? = null) {
        val existing = if (editingId != null) state.addresses.find { it.id == editingId } else null
        state = state.copy(
            showAddForm = true,
            editingId = editingId,
            newAddressText = existing?.fullAddress ?: "",
            newAddressLabel = existing?.label ?: "Home"
        )
    }

    fun closeAddForm() {
        state = state.copy(showAddForm = false, newAddressText = "", newAddressLabel = "Home", editingId = null)
    }

    fun updateSearch(query: String) {
        state = state.copy(searchQuery = query)
    }

    fun updateNewAddressText(value: String) {
        state = state.copy(newAddressText = value)
    }

    fun updateNewAddressLabel(value: String) {
        state = state.copy(newAddressLabel = value)
    }

    fun saveAddress() {
        val text = state.newAddressText.trim()
        if (text.isBlank()) return
        val editId = state.editingId
        if (editId != null) addressStorage.remove(editId)
        val newId = editId ?: "addr_${System.currentTimeMillis()}"
        addressStorage.save(SavedAddress(id = newId, label = state.newAddressLabel, fullAddress = text))
        if (state.activeAddressId == null || editId == state.activeAddressId) {
            addressStorage.setActiveId(newId)
        }
        val all = addressStorage.getAll()
        state = state.copy(
            addresses = all,
            activeAddressId = addressStorage.getActiveId(),
            showAddForm = false,
            newAddressText = "",
            newAddressLabel = "Home",
            editingId = null
        )
    }
}
