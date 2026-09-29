package com.binshahab.dairyfarmpro

data class Purchase(
    val id: String,
    val farmId: String,
    val animalId: String,
    val purchaseDate: String,
    val purchasePrice: Double,
    val sellerName: String = "",
    val sellerPhone: String = "",
    val notes: String = ""
)
