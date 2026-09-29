package com.binshahab.dairyfarmpro

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object PurchaseRepository {

    private const val PREFS_NAME = "purchase_records"
    private const val KEY_PURCHASES = "purchases"

    private fun getPrefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getAll(context: Context): MutableList<Purchase> {
        val list = mutableListOf<Purchase>()
        val json = getPrefs(context).getString(KEY_PURCHASES, "[]") ?: "[]"

        val array = JSONArray(json)

        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)

            list.add(
                Purchase(
                    id = obj.getString("id"),
                    farmId = obj.getString("farmId"),
                    animalId = obj.getString("animalId"),
                    purchaseDate = obj.getString("purchaseDate"),
                    purchasePrice = obj.getDouble("purchasePrice"),
                    sellerName = obj.optString("sellerName", ""),
                    sellerPhone = obj.optString("sellerPhone", ""),
                    notes = obj.optString("notes", "")
                )
            )
        }

        return list
    }

    fun add(context: Context, purchase: Purchase) {
        val list = getAll(context)
        list.add(purchase)
        saveAll(context, list)
    }

    fun update(context: Context, purchase: Purchase) {
        val list = getAll(context)

        val index = list.indexOfFirst { it.id == purchase.id }

        if (index >= 0) {
            list[index] = purchase
            saveAll(context, list)
        }
    }

    fun delete(context: Context, purchaseId: String) {
        val list = getAll(context)

        list.removeAll { it.id == purchaseId }

        saveAll(context, list)
    }

    fun getForAnimal(
        context: Context,
        animalId: String
    ): List<Purchase> {
        return getAll(context).filter {
            it.animalId == animalId
        }
    }

    private fun saveAll(
        context: Context,
        list: List<Purchase>
    ) {
        val array = JSONArray()

        for (purchase in list) {
            val obj = JSONObject()

            obj.put("id", purchase.id)
            obj.put("farmId", purchase.farmId)
            obj.put("animalId", purchase.animalId)
            obj.put("purchaseDate", purchase.purchaseDate)
            obj.put("purchasePrice", purchase.purchasePrice)
            obj.put("sellerName", purchase.sellerName)
            obj.put("sellerPhone", purchase.sellerPhone)
            obj.put("notes", purchase.notes)

            array.put(obj)
        }

        getPrefs(context)
            .edit()
            .putString(KEY_PURCHASES, array.toString())
            .apply()
    }
}
