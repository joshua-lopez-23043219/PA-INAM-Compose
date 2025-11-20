package com.example.pa_inam.network

import com.google.gson.JsonElement
import com.google.gson.JsonObject

// Convierte cualquier JsonElement a Double de forma segura
fun JsonElement?.asSafeDouble(): Double {
    if (this == null || isJsonNull) return 0.0
    return when {
        isJsonPrimitive && asJsonPrimitive.isNumber -> asDouble
        isJsonPrimitive && asJsonPrimitive.isString -> asString.toDoubleOrNull() ?: 0.0
        else -> 0.0
    }
}

// Busca la primera clave que cumpla un predicado
private fun JsonObject.findKey(predicate: (String) -> Boolean): String? {
    return keySet().firstOrNull(predicate)
}

// Mapea filas genéricas a una serie etiqueta-valor
fun List<OlapRow>.toCategorySeries(
    labelKeySelector: (Set<String>) -> String,
    valueKeySelector: (Set<String>) -> String
): List<CategoryValue> {
    if (isEmpty()) return emptyList()

    val sampleKeys = first().keySet()
    val labelKey = labelKeySelector(sampleKeys)
    val valueKey = valueKeySelector(sampleKeys)

    return map { row ->
        val label = row[labelKey]?.toString()?.replace("\"", "") ?: ""
        val value = row[valueKey].asSafeDouble()
        CategoryValue(label = label, value = value)
    }
}

// Convierte valores del Map<String, Any?> en Double de forma segura
fun parseDoubleSafe(value: Any?): Double = when (value) {
    is Number -> value.toDouble()
    is String -> value.toDoubleOrNull() ?: 0.0
    else      -> 0.0
}


// Versión específica para Year + Count (por si cambian los nombres)
fun List<OlapRow>.toYearCountSeries(): List<CategoryValue> {
    if (isEmpty()) return emptyList()
    val keys = first().keySet()

    val yearKey = keys.find { it.contains("year", ignoreCase = true) } ?: keys.first()
    val countKey = keys.find {
        it.contains("count", true) || it.contains("recuento", true) || it.contains("measure", true)
    } ?: (keys - yearKey).firstOrNull() ?: yearKey

    return map { row ->
        CategoryValue(
            label = row[yearKey]?.toString()?.replace("\"", "") ?: "",
            value = row[countKey].asSafeDouble()
        )
    }
}



fun List<OlapRow>.toGenderStats(): GenderStats {
    if (isEmpty()) return GenderStats(0.0, 0.0)

    val row = first()
    val keysList = row.keySet().toList()

    // 1) Intentar detectar por nombre (Hombres / Mujeres)
    val hombresKeyByName = keysList.firstOrNull {
        it.contains("hombre", ignoreCase = true) ||
                it.contains("hombres", ignoreCase = true) ||
                it.contains("male", ignoreCase = true)
    }

    val mujeresKeyByName = keysList.firstOrNull {
        it != hombresKeyByName && (
                it.contains("mujer", ignoreCase = true) ||
                        it.contains("mujeres", ignoreCase = true) ||
                        it.contains("female", ignoreCase = true)
                )
    }

    if (hombresKeyByName != null && mujeresKeyByName != null) {
        val hombres = row[hombresKeyByName].asSafeDouble()
        val mujeres = row[mujeresKeyByName].asSafeDouble()
        return GenderStats(males = hombres, females = mujeres)
    }

    // 2) Plan B: tomar las 2 primeras columnas tal cual vengan
    //    (nuestro MDX siempre pone primero Hombres, luego Mujeres)
    if (keysList.size >= 2) {
        val k1 = keysList[0]
        val k2 = keysList[1]

        val hombres = row[k1].asSafeDouble()
        val mujeres = row[k2].asSafeDouble()

        return GenderStats(males = hombres, females = mujeres)
    }

    // 3) Si solo hay 1 columna numérica, la usamos como Hombres
    if (keysList.size == 1) {
        val único = row[keysList[0]].asSafeDouble()
        return GenderStats(males = único, females = 0.0)
    }

    // 4) Fallback final
    return GenderStats(0.0, 0.0)
}