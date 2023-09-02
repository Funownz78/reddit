package ru.fwnz.humblr.data.dto.postsList


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Children(
    @Json(name = "data")
    var `data`: PostDto = PostDto(),
//    @Json(name = "kind")
//    var kind: String = ""
)