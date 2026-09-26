package com.binshahab.dairyfarmpro

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class AnimalRepository(context: Context) {

    private val preferences =
        context.getSharedPreferences("dairyfarmpro_animals", Context.MODE_PRIVATE)

    private fun key(farmId: String) = "animals_$farmId"

    fun getAnimals(farmId: String): MutableList<Animal> {
        val result = mutableListOf<Animal>()
        val json = preferences.getString(key(farmId), "[]") ?: "[]"

        try {
            val array = JSONArray(json)

            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)

                result.add(
                    Animal(
                        id = item.optString("id"),
                        name = item.optString("name"),
                        tag = item.optString("tag"),
                        type = item.optString("type"),
                        breed = item.optString("breed"),
                        gender = item.optString("gender"),
                        birthDate = item.optString("birthDate"),
                        purchaseDate = item.optString("purchaseDate"),
                        purchasePrice = item.optDouble("purchasePrice", 0.0),
                        saleDate = item.optString("saleDate"),
                        salePrice = item.optDouble("salePrice", 0.0),
                        status = item.optString("status", "Active")
                    )
                )
            }
        } catch (_: Exception) {
            // If stored data is damaged, return an empty list
        }

        return result
    }

    fun saveAnimals(farmId: String, animals: List<Animal>) {
        val array = JSONArray()

        animals.forEach { animal ->
            val item = JSONObject()

            item.put("id", animal.id)
            item.put("name", animal.name)
            item.put("tag", animal.tag)
            item.put("type", animal.type)
            item.put("breed", animal.breed)
            item.put("gender", animal.gender)
            item.put("birthDate", animal.birthDate)
            item.put("purchaseDate", animal.purchaseDate)
            item.put("purchasePrice", animal.purchasePrice)
            item.put("saleDate", animal.saleDate)
            item.put("salePrice", animal.salePrice)
            item.put("status", animal.status)

            array.put(item)
        }

        preferences.edit()
            .putString(key(farmId), array.toString())
            .apply()
    }

    fun addAnimal(farmId: String, animal: Animal): Boolean {
        val animals = getAnimals(farmId)

        if (animals.any { it.id.equals(animal.id, ignoreCase = true) }) {
            return false
        }

        if (animals.any { it.name.equals(animal.name, ignoreCase = true) }) {
            return false
        }

        if (animals.any { it.tag.equals(animal.tag, ignoreCase = true) }) {
            return false
        }

        animals.add(animal)
        saveAnimals(farmId, animals)
        return true
    }

    fun updateAnimal(
        farmId: String,
        oldId: String,
        updatedAnimal: Animal
    ): Boolean {
        val animals = getAnimals(farmId)
        val index = animals.indexOfFirst {
            it.id.equals(oldId, ignoreCase = true)
        }

        if (index == -1) return false

        val duplicate = animals.any {
            !it.id.equals(oldId, ignoreCase = true) &&
                (
                    it.id.equals(updatedAnimal.id, ignoreCase = true) ||
                    it.name.equals(updatedAnimal.name, ignoreCase = true) ||
                    it.tag.equals(updatedAnimal.tag, ignoreCase = true)
                )
        }

        if (duplicate) return false

        animals[index] = updatedAnimal
        saveAnimals(farmId, animals)
        return true
    }

    fun deleteAnimal(farmId: String, animalId: String): Boolean {
        val animals = getAnimals(farmId)
        val removed = animals.removeAll {
            it.id.equals(animalId, ignoreCase = true)
        }

        if (removed) {
            saveAnimals(farmId, animals)
        }

        return removed
    }

    fun findAnimal(farmId: String, animalId: String): Animal? {
        return getAnimals(farmId).firstOrNull {
            it.id.equals(animalId, ignoreCase = true)
        }
    }

    fun markSold(
        farmId: String,
        animalId: String,
        saleDate: String,
        salePrice: Double
    ): Boolean {
        val animals = getAnimals(farmId)

        val animal = animals.firstOrNull {
            it.id.equals(animalId, ignoreCase = true)
        } ?: return false

        if (animal.status.equals("Sold", ignoreCase = true)) {
            return false
        }

        animal.saleDate = saleDate
        animal.salePrice = salePrice
        animal.status = "Sold"

        saveAnimals(farmId, animals)
        return true
    }
}
