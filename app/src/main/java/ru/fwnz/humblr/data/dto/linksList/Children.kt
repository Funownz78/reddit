package ru.fwnz.humblr.data.dto.linksList


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Children(
    @Json(name = "data")
    var `data`: DataX = DataX(),
    @Json(name = "kind")
    var kind: String = ""
)