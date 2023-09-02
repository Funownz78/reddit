package ru.fwnz.humblr.data.dto.commentsByUser


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CommentsByUserDto(
    @Json(name = "data")
    var `data`: Data = Data(),
    @Json(name = "kind")
    var kind: String = ""
)