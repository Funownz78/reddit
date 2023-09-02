package ru.fwnz.humblr.data.dto.linksList


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Data(
    @Json(name = "after")
    var after: String = "",
    @Json(name = "before")
    var before: String? = null,
    @Json(name = "children")
    var children: List<Children> = listOf(),
    @Json(name = "dist")
    var dist: Int = 0,
    @Json(name = "geo_filter")
    var geoFilter: Any? = Any(),
    @Json(name = "modhash")
    var modhash: Any? = Any()
)