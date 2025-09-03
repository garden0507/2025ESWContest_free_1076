package com.example.smartWardrobeApp.weather

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object WeatherClient {
    @Volatile var latitude: Double = 37.5665
    @Volatile var longitude: Double = 126.9780
    @Volatile var offlineMode: Boolean = false
    @Volatile var mockTempC: Double = 28.0
    @Volatile var mockHumidityPct: Int = 60

    data class WeatherNow(val tempC: Double?, val humidityPct: Int?)

    suspend fun fetchTempAndHumidity(): WeatherNow = withContext(Dispatchers.IO) {
        if (offlineMode) return@withContext WeatherNow(mockTempC, mockHumidityPct)

        val urlStr =
            "https://api.open-meteo.com/v1/forecast" +
                    "?latitude=$latitude&longitude=$longitude" +
                    "&current_weather=true" +
                    "&hourly=relative_humidity_2m&timezone=auto" +
                    "&past_days=1&forecast_days=1"

        val conn = (URL(urlStr).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 6000
            readTimeout = 6000
        }

        try {
            val code = conn.responseCode
            if (code !in 200..299) return@withContext WeatherNow(null, null)

            val txt = conn.inputStream.bufferedReader().use { it.readText() }
            val root = JSONObject(txt)


            val cw = root.optJSONObject("current_weather")
            val temp = cw?.optDouble("temperature", Double.NaN)?.takeIf { !it.isNaN() }
            val cwTime = cw?.optString("time", null) // 예: "2025-08-22T11:00"


            var humidity: Int? = null
            val hourly = root.optJSONObject("hourly")
            val times  = hourly?.optJSONArray("time")
            val hums   = hourly?.optJSONArray("relative_humidity_2m")
            if (times != null && hums != null && times.length() == hums.length() && hums.length() > 0) {
                var idx = -1
                if (cwTime != null) {
                    for (i in 0 until times.length()) {
                        if (times.optString(i, "") == cwTime) { idx = i; break }
                    }
                }
                if (idx == -1) idx = hums.length() - 1 // 매칭 실패 시 최신값
                val hv = hums.optInt(idx, -1)
                if (hv in 0..100) humidity = hv
            }

            WeatherNow(temp, humidity)
        } catch (_: Exception) {
            WeatherNow(null, null)
        } finally {
            runCatching { conn.disconnect() }
        }
    }
}
