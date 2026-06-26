package com.alculator.data

data class Drink(
    val id: String,
    val name: String,
    val price: Double,
    val abv: Double,
    val volumeMl: Double,           // total volume (singleVolumeMl × quantity)
    val singleVolumeMl: Double = volumeMl,
    val quantity: Int = 1
) {
    val units: Double get() = abv * volumeMl / 1000.0
    val unitsPerPound: Double get() = if (price > 0.0) units / price else 0.0
    val costPerUnit: Double get() = if (units > 0.0) price / units else Double.MAX_VALUE
}
