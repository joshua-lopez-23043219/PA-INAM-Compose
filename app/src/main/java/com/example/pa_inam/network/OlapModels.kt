package com.example.pa_inam.network

// Serie genérica: etiqueta + valor (para barras, líneas, etc.)
data class CategoryValue(
    val label: String,
    val value: Double
)

// Aprobados vs Reprobados
data class PassFailStats(
    val passed: Double,
    val failed: Double
)

data class GenderStats(
    val males: Double,
    val females: Double
) {
    val total: Double
        get() = males + females
}

