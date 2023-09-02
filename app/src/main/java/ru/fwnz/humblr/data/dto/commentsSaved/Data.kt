package ru.fwnz.humblr.data.dto.commentsSaved


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Data(
//    @Json(name = "after")
//    var after: Any? = Any(),
//    @Json(name = "before")
//    var before: Any? = Any(),
    @Json(name = "children")
    var children: List<Children> = listOf(),
//    @Json(name = "dist")
//    var dist: Int = 0,
//    @Json(name = "geo_filter")
//    var geoFilter: String = "",
//    @Json(name = "modhash")
//    var modhash: Any? = Any()
)