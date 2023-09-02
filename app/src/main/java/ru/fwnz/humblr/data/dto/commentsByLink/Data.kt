package ru.fwnz.humblr.data.dto.commentsByLink


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Data(
    @Json(name = "after")
    var after: String? = "",
    @Json(name = "before")
    var before: String? = null,
    @Json(name = "children")
    var children: List<Children> = listOf(),
    @Json(name = "dist")
    var dist: Int? = 0,
//    @Json(name = "geo_filter")
//    var geoFilter: String = "",
//    @Json(name = "modhash")
//    var modhash: Any? = Any()
)