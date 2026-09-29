package com.binshahab.dairyfarmpro

data class AnimalSale(
    val id: String,
    val farmId: String,
    val animalId: String,
    val saleDate: String,
    val purchaseDate: String,
    val purchasePrice: Double,
    val salePrice: Double,
    val buyerName: String = "",
    val buyerPhone: String = "",
    val notes: String = ""
) {
    val saleProfit: Double
        get() = salePrice - purchasePrice
}
