package com.binshahab.dairyfarmpro

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class AnimalSaleRepository(context: Context) {

    private val preferences =
        context.getSharedPreferences("dairyfarmpro_sales", Context.MODE_PRIVATE)

    private fun key(farmId: String) = "sales_$farmId"

    fun getSales(farmId: String): MutableList<AnimalSale> {
        val result = mutableListOf<AnimalSale>()
        val json = preferences.getString(key(farmId), "[]") ?: "[]"

        try {
            val array = JSONArray(json)

            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)

                result.add(
                    AnimalSale(
                        id = item.optString("id"),
                        farmId = item.optString("farmId"),
                        animalId = item.optString("animalId"),
                        saleDate = item.optString("saleDate"),
                        purchaseDate = item.optString("purchaseDate"),
                        purchasePrice = item.optDouble("purchasePrice", 0.0),
                        salePrice = item.optDouble("salePrice", 0.0),
                        buyerName = item.optString("buyerName"),
                        buyerPhone = item.optString("buyerPhone"),
                        notes = item.optString("notes")
                    )
                )
            }
        } catch (_: Exception) {
            // Return empty list if stored data is damaged.
        }

        return result
    }

    fun saveSales(
        farmId: String,
        sales: List<AnimalSale>
    ) {
        val array = JSONArray()

        sales.forEach { sale ->
            val item = JSONObject()

            item.put("id", sale.id)
            item.put("farmId", sale.farmId)
            item.put("animalId", sale.animalId)
            item.put("saleDate", sale.saleDate)
            item.put("purchaseDate", sale.purchaseDate)
            item.put("purchasePrice", sale.purchasePrice)
            item.put("salePrice", sale.salePrice)
            item.put("buyerName", sale.buyerName)
            item.put("buyerPhone", sale.buyerPhone)
            item.put("notes", sale.notes)

            array.put(item)
        }

        preferences.edit()
            .putString(key(farmId), array.toString())
            .apply()
    }

    fun addSale(
        farmId: String,
        sale: AnimalSale
    ): Boolean {
        val sales = getSales(farmId)

        val alreadySold = sales.any {
            it.animalId.equals(sale.animalId, ignoreCase = true)
        }

        if (alreadySold) {
            return false
        }

        sales.add(sale)
        saveSales(farmId, sales)

        return true
    }

    fun deleteSale(
        farmId: String,
        saleId: String
    ): Boolean {
        val sales = getSales(farmId)

        val removed = sales.removeAll {
            it.id.equals(saleId, ignoreCase = true)
        }

        if (removed) {
            saveSales(farmId, sales)
        }

        return removed
    }

    fun getSalesForAnimal(
        farmId: String,
        animalId: String
    ): MutableList<AnimalSale> {
        return getSales(farmId)
            .filter {
                it.animalId.equals(animalId, ignoreCase = true)
            }
            .toMutableList()
    }

    fun getSalesForDate(
        farmId: String,
        date: String
    ): MutableList<AnimalSale> {
        return getSales(farmId)
            .filter { it.saleDate == date }
            .toMutableList()
    }
}
