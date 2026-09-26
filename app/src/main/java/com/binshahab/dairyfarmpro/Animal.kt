package com.binshahab.dairyfarmpro

data class Animal(
    val id: String,
    val name: String,
    val tag: String,
    val type: String,
    val breed: String,
    val gender: String,
    val birthDate: String,
    val purchaseDate: String,
    val purchasePrice: Double,
    var saleDate: String = "",
    var salePrice: Double = 0.0,
    var status: String = "Active"
)
