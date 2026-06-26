package com.alculator.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class ProductInfo(
    val name: String?,
    val abv: Double?,
    val volumeMl: Double?
)

suspend fun lookupBarcode(barcode: String): ProductInfo? = withContext(Dispatchers.IO) {
    try {
        val url = URL("https://world.openfoodfacts.org/api/v0/product/$barcode.json")
        val conn = url.openConnection() as HttpURLConnection
        conn.connectTimeout = 6000
        conn.readTimeout = 6000
        conn.setRequestProperty("User-Agent", "the-alculator/1.1 (github.com/IvoryCobra-VC/the-alculator)")
        if (conn.responseCode != 200) return@withContext null
        val json = JSONObject(conn.inputStream.bufferedReader().readText())
        if (json.optInt("status") != 1) return@withContext null
        val product = json.optJSONObject("product") ?: return@withContext null

        val name = product.optString("product_name").takeIf { it.isNotBlank() }

        val abv = product.optJSONObject("nutriments")
            ?.takeIf { it.has("alcohol_100g") }
            ?.getDouble("alcohol_100g")

        val volumeMl = parseVolume(product.optString("quantity"))

        ProductInfo(name = name, abv = abv, volumeMl = volumeMl)
    } catch (_: Exception) {
        null
    }
}

private val volumeRegex = Regex("""(\d+\.?\d*)\s*(ml|cl|l)""", RegexOption.IGNORE_CASE)

private fun parseVolume(quantity: String): Double? {
    val match = volumeRegex.find(quantity) ?: return null
    val value = match.groupValues[1].toDoubleOrNull() ?: return null
    return when (match.groupValues[2].lowercase()) {
        "ml" -> value
        "cl" -> value * 10.0
        "l"  -> value * 1000.0
        else -> null
    }
}
