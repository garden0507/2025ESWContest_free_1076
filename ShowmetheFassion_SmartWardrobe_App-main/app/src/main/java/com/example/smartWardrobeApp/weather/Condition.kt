package com.example.smartWardrobeApp.weather

enum class LengthFilter { SHORT_ONLY, LONG_ONLY, ANY }

fun lengthFilterFromTempHum(needType: String, tempC: Double?, humidityPct: Int?):
        LengthFilter {
            if (tempC == null) return LengthFilter.ANY

            else if (tempC > 28.0) return LengthFilter.SHORT_ONLY

            else if (tempC > 25.0 && (humidityPct != null && humidityPct > 70)) {
                return LengthFilter.SHORT_ONLY
                }

            else if (tempC > 25.0 && tempC < 28.0 && (humidityPct == null || humidityPct <= 70)) {
                return if (needType == "shirt") LengthFilter.SHORT_ONLY else LengthFilter.LONG_ONLY
                }

            else if (tempC < 25.0 && (humidityPct == null || humidityPct <= 70)) {
                return LengthFilter.LONG_ONLY
                }

            else return LengthFilter.ANY
}
