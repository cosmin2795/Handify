package com.example.handify.domain.source

import com.example.handify.domain.model.SavedAddress

interface AddressStorage {
    fun getAll(): List<SavedAddress>
    fun save(address: SavedAddress)
    fun remove(id: String)
    fun getActiveId(): String?
    fun setActiveId(id: String?)
}
