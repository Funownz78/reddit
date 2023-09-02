package ru.fwnz.humblr.data.dto.linksList


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LinksListDto(
    @Json(name = "data")
    var `data`: Data = Data(),
    @Json(name = "kind")
    var kind: String = ""
)