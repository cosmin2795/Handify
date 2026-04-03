package com.example.handify.data.source

import android.content.Context
import com.example.handify.domain.model.SavedAddress
import com.example.handify.domain.source.AddressStorage
import org.json.JSONArray
import org.json.JSONObject

class AndroidAddressStorage(context: Context) : AddressStorage {

    private val prefs = context.getSharedPreferences("handify_addresses", Context.MODE_PRIVATE)

    init {
        if (prefs.getString("addresses", null) == null) {
            save(SavedAddress("addr_1", "Home", "123 Oak Street, Austin, TX"))
            save(SavedAddress("addr_2", "Work", "456 Commerce Blvd, Austin, TX"))
            setActiveId("addr_1")
        }
    }

    override fun getAll(): List<SavedAddress> {
        val json = prefs.getString("addresses", "[]") ?: "[]"
        val arr = JSONArray(json)
        return (0 until arr.length()).map { i ->
            val obj = arr.getJSONObject(i)
            SavedAddress(
                id = obj.getString("id"),
                label = obj.getString("label"),
                fullAddress = obj.getString("fullAddress")
            )
        }
    }

    override fun save(address: SavedAddress) {
        val current = getAll().toMutableList()
        current.removeAll { it.id == address.id }
        current.add(address)
        persist(current)
    }

    override fun remove(id: String) {
        val current = getAll().filter { it.id != id }
        persist(current)
    }

    override fun getActiveId(): String? = prefs.getString("active_id", null)

    override fun setActiveId(id: String?) {
        prefs.edit().apply {
            if (id == null) remove("active_id") else putString("active_id", id)
            apply()
        }
    }

    private fun persist(addresses: List<SavedAddress>) {
        val arr = JSONArray()
        addresses.forEach { a ->
            arr.put(JSONObject().apply {
                put("id", a.id)
                put("label", a.label)
                put("fullAddress", a.fullAddress)
            })
        }
        prefs.edit().putString("addresses", arr.toString()).apply()
    }
}
