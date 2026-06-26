package com.alculator.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

private const val UA = "the-alculator/1.5 (github.com/IvoryCobra-VC/the-alculator)"

suspend fun searchOnline(query: String): List<SearchResult> = withContext(Dispatchers.IO) {
    try {
        val encoded = URLEncoder.encode(query, "UTF-8")
        val url = URL(
            "https://world.openfoodfacts.org/cgi/search.pl" +
            "?search_terms=$encoded&action=process&json=1" +
            "&fields=product_name,quantity,nutriments,alcohol_content&page_size=8"
        )
        val conn = url.openConnection() as HttpURLConnection
        conn.connectTimeout = 6000
        conn.readTimeout = 6000
        conn.setRequestProperty("User-Agent", UA)
        if (conn.responseCode != 200) return@withContext emptyList()
        val json = JSONObject(conn.inputStream.bufferedReader().readText())
        val products = json.optJSONArray("products") ?: return@withContext emptyList()
        (0 until products.length()).mapNotNull { i ->
            val p = products.optJSONObject(i) ?: return@mapNotNull null
            val name = p.optString("product_name").takeIf { it.isNotBlank() }
                ?: return@mapNotNull null
            val abv = p.optJSONObject("nutriments")
                ?.takeIf { it.has("alcohol_100g") }
                ?.getDouble("alcohol_100g")
                ?: parseAbv(p.optString("alcohol_content"))
            val volumeMl = parseVolume(p.optString("quantity"))
            SearchResult(name = name, abv = abv, volumeMl = volumeMl)
        }
    } catch (_: Exception) {
        emptyList()
    }
}

private val abvRegex = Regex("""(\d+\.?\d*)\s*%""")
private fun parseAbv(content: String): Double? =
    abvRegex.find(content)?.groupValues?.get(1)?.toDoubleOrNull()

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
