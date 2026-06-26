package com.alculator.data

enum class VolumeUnit(val label: String) {
    ML("ml"), CL("cl"), L("L"), PINT("pint");

    fun toMl(value: Double): Double = when (this) {
        ML -> value
        CL -> value * 10.0
        L -> value * 1000.0
        PINT -> value * 568.261
    }
}

enum class VolumePreset(val label: String, val ml: Int) {
    CAN_330("330ml", 330),
    CAN_440("440ml", 440),
    PINT("Pint", 568),
    BOTTLE("750ml", 750)
}
