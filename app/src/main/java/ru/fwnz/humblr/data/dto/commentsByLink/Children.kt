package ru.fwnz.humblr.data.dto.commentsByLink


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Children(
    @Json(name = "data")
    var `data`: Any? = null,
    @Json(name = "kind")
    var kind: String = ""
)