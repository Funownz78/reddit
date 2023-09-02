package ru.fwnz.humblr.data.dto.commentsSaved


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CommentsSavedDto(
    @Json(name = "data")
    var `data`: Data = Data(),
//    @Json(name = "kind")
//    var kind: String = ""
)