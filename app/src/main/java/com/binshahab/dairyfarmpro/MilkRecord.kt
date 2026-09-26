package com.binshahab.dairyfarmpro

data class MilkRecord(
    val id: String,
    val farmId: String,
    val animalId: String,
    val date: String,
    var morningKg: Double = 0.0,
    var eveningKg: Double = 0.0,
    var pricePerKg: Double = 0.0,
    var milkGivenKg: Double = 0.0
) {
    val totalKg: Double
        get() = morningKg + eveningKg

    val remainingKg: Double
        get() = totalKg - milkGivenKg

    val milkIncome: Double
        get() = remainingKg * pricePerKg
}
