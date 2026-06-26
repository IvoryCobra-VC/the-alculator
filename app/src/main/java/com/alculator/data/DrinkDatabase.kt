package com.alculator.data

data class SearchResult(
    val name: String,
    val abv: Double?,
    val volumeMl: Double?
)

private val KNOWN_DRINKS = listOf(
    // Lagers & Beers
    SearchResult("Stella Artois", 4.6, 440.0),
    SearchResult("Peroni Nastro Azzurro", 5.1, 330.0),
    SearchResult("Fosters", 4.0, 440.0),
    SearchResult("Carlsberg", 3.8, 440.0),
    SearchResult("Carling", 4.0, 440.0),
    SearchResult("Budweiser", 4.5, 440.0),
    SearchResult("Heineken", 5.0, 330.0),
    SearchResult("Corona Extra", 4.6, 330.0),
    SearchResult("Kronenbourg 1664", 5.0, 440.0),
    SearchResult("San Miguel", 5.0, 330.0),
    SearchResult("Desperados", 5.9, 330.0),
    SearchResult("Birra Moretti", 4.6, 330.0),
    SearchResult("Madri Excepcional", 4.6, 440.0),
    SearchResult("Amstel", 4.0, 440.0),
    SearchResult("Becks", 4.0, 275.0),
    SearchResult("Cobra", 4.5, 330.0),
    SearchResult("Kingfisher", 4.8, 330.0),
    SearchResult("Modelo Especial", 4.4, 330.0),
    SearchResult("Estrella Damm", 5.4, 330.0),
    SearchResult("Asahi Super Dry", 5.0, 330.0),
    SearchResult("Tiger Beer", 5.0, 330.0),
    SearchResult("Efes", 5.0, 500.0),
    SearchResult("Leffe Blonde", 6.6, 330.0),
    SearchResult("Erdinger Weissbier", 5.3, 500.0),
    SearchResult("Hoegaarden", 4.9, 330.0),
    SearchResult("Paulaner Hefe-Weissbier", 5.5, 500.0),
    SearchResult("Staropramen", 5.0, 330.0),
    SearchResult("Budvar", 5.0, 330.0),
    // Ales & Stouts
    SearchResult("Guinness Draught", 4.1, 440.0),
    SearchResult("Guinness Original", 4.2, 330.0),
    SearchResult("Old Speckled Hen", 5.0, 500.0),
    SearchResult("Doom Bar", 4.0, 500.0),
    SearchResult("London Pride", 4.1, 500.0),
    SearchResult("Hobgoblin Gold", 4.2, 500.0),
    SearchResult("Boddingtons Pub Ale", 3.8, 440.0),
    SearchResult("Newcastle Brown Ale", 4.7, 500.0),
    SearchResult("Bombardier", 4.3, 500.0),
    // Ciders
    SearchResult("Strongbow Original", 5.0, 440.0),
    SearchResult("Strongbow Dark Fruit", 4.0, 440.0),
    SearchResult("Magners Original", 4.5, 568.0),
    SearchResult("Bulmers Original", 4.5, 568.0),
    SearchResult("Kopparberg Mixed Fruit", 4.0, 500.0),
    SearchResult("Kopparberg Strawberry & Lime", 4.0, 500.0),
    SearchResult("Rekorderlig Strawberry & Lime", 4.0, 500.0),
    SearchResult("Thatchers Gold", 4.8, 500.0),
    SearchResult("Aspall Suffolk Cyder", 5.5, 500.0),
    SearchResult("Somersby Apple", 4.5, 440.0),
    // Spirits (standard 700ml bottle)
    SearchResult("Gordon's London Dry Gin", 37.5, 700.0),
    SearchResult("Gordon's Pink Gin", 37.5, 700.0),
    SearchResult("Tanqueray London Dry Gin", 43.1, 700.0),
    SearchResult("Hendrick's Gin", 41.4, 700.0),
    SearchResult("Bombay Sapphire", 40.0, 700.0),
    SearchResult("Smirnoff Red Label Vodka", 37.5, 700.0),
    SearchResult("Absolut Vodka", 40.0, 700.0),
    SearchResult("Grey Goose Vodka", 40.0, 700.0),
    SearchResult("Jack Daniel's Old No.7", 40.0, 700.0),
    SearchResult("Famous Grouse Whisky", 40.0, 700.0),
    SearchResult("Jameson Irish Whiskey", 40.0, 700.0),
    SearchResult("Glenfiddich 12 Year Old", 40.0, 700.0),
    SearchResult("Laphroaig 10 Year Old", 40.0, 700.0),
    SearchResult("Captain Morgan Spiced Gold", 35.0, 700.0),
    SearchResult("Bacardi Carta Blanca", 37.5, 700.0),
    SearchResult("Havana Club 3 Year Old", 40.0, 700.0),
    SearchResult("Baileys Irish Cream", 17.0, 700.0),
    SearchResult("Malibu", 21.0, 700.0),
    SearchResult("Archers Schnapps", 18.0, 700.0),
    SearchResult("Disaronno Amaretto", 28.0, 700.0),
    SearchResult("Jägermeister", 35.0, 700.0),
    // RTDs
    SearchResult("WKD Blue", 4.0, 275.0),
    SearchResult("Smirnoff Ice", 4.0, 275.0),
    SearchResult("Hooch", 4.0, 275.0),
    SearchResult("VK Blue", 4.0, 275.0),
    SearchResult("Bacardi Breezer", 4.0, 275.0),
    SearchResult("Kopparberg Passion Fruit & Orange Gin", 4.0, 250.0),
    // Wine (generic — ABV varies, these are typical)
    SearchResult("Prosecco", 11.0, 750.0),
    SearchResult("Champagne", 12.0, 750.0),
)

fun searchLocal(query: String): List<SearchResult> {
    if (query.length < 2) return emptyList()
    val q = query.lowercase()
    return KNOWN_DRINKS
        .filter { it.name.lowercase().contains(q) }
        .sortedBy { if (it.name.lowercase().startsWith(q)) 0 else 1 }
        .take(6)
}
