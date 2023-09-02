package ru.fwnz.humblr.data.dto.postsList


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostsListDto(
    @Json(name = "data")
    var `data`: Data = Data(),
//    @Json(name = "kind")
//    var kind: String = ""
)