package ru.fwnz.humblr.data.dto.commentsByUser


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthorFlairRichtext(
    @Json(name = "a")
    var a: String? = "",
    @Json(name = "e")
    var e: String = "",
    @Json(name = "t")
    var t: String? = "",
    @Json(name = "u")
    var u: String? = ""
)