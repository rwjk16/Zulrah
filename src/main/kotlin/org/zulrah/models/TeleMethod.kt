package org.zulrah.models


object TeleMethod {
   const val ZUL = "Zul-Andra teleport"
    const val DRAMEN = "Dramen staff"
    const val LUNAR = "Lunar staff"
    const val CATHERBY = "Catherby teleport"


}

//enum class TeleMethod(val rawValue: String) {
//    ZUL("Zul-Andra teleport"),
//    DRAMEN("Dramen staff"),
//    LUNAR("Lunar staff"),
//    CATHERBY("Catherby teleport");
//
//    companion object {
//        const val allTeles = listOf<String>(ZUL.rawValue, DRAMEN.rawValue, LUNAR.rawValue, CATHERBY.rawValue)
//
//        fun fromRawValue(rawValue: String): TeleMethod? = values().find { it.rawValue == rawValue }
//
//    }
//}