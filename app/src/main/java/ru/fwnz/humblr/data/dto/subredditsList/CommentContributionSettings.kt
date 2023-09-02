package ru.fwnz.humblr.data.dto.subredditsList


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CommentContributionSettings(
    @Json(name = "allowed_media_types")
    var allowedMediaTypes: List<String>? = listOf()
)