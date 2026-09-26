package com.binshahab.dairyfarmpro

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class MilkRepository(context: Context) {

    private val preferences =
        context.getSharedPreferences("dairyfarmpro_milk", Context.MODE_PRIVATE)

    private fun key(farmId: String) = "milk_$farmId"

    fun getRecords(farmId: String): MutableList<MilkRecord> {
        val result = mutableListOf<MilkRecord>()
        val json = preferences.getString(key(farmId), "[]") ?: "[]"

        try {
            val array = JSONArray(json)

            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)

                result.add(
                    MilkRecord(
                        id = item.optString("id"),
                        farmId = item.optString("farmId"),
                        animalId = item.optString("animalId"),
                        date = item.optString("date"),
                        morningKg = item.optDouble("morningKg", 0.0),
                        eveningKg = item.optDouble("eveningKg", 0.0),
                        pricePerKg = item.optDouble("pricePerKg", 0.0),
                        milkGivenKg = item.optDouble("milkGivenKg", 0.0)
                    )
                )
            }
        } catch (_: Exception) {
            // Return empty list if stored data is damaged.
        }

        return result
    }

    fun saveRecords(
        farmId: String,
        records: List<MilkRecord>
    ) {
        val array = JSONArray()

        records.forEach { record ->
            val item = JSONObject()

            item.put("id", record.id)
            item.put("farmId", record.farmId)
            item.put("animalId", record.animalId)
            item.put("date", record.date)
            item.put("morningKg", record.morningKg)
            item.put("eveningKg", record.eveningKg)
            item.put("pricePerKg", record.pricePerKg)
            item.put("milkGivenKg", record.milkGivenKg)

            array.put(item)
        }

        preferences.edit()
            .putString(key(farmId), array.toString())
            .apply()
    }

    fun addRecord(
        farmId: String,
        record: MilkRecord
    ) {
        val records = getRecords(farmId)

        records.removeAll {
            it.animalId.equals(record.animalId, ignoreCase = true) &&
                it.date == record.date
        }

        records.add(record)
        saveRecords(farmId, records)
    }

    fun deleteRecord(
        farmId: String,
        recordId: String
    ): Boolean {
        val records = getRecords(farmId)

        val removed = records.removeAll {
            it.id.equals(recordId, ignoreCase = true)
        }

        if (removed) {
            saveRecords(farmId, records)
        }

        return removed
    }

    fun getRecordsForDate(
        farmId: String,
        date: String
    ): MutableList<MilkRecord> {
        return getRecords(farmId)
            .filter { it.date == date }
            .toMutableList()
    }
}
