package ru.fwnz.humblr.data.dto.commentsByLink


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NodeWithReplies(
    @Json(name = "author")
    override var author: String = "",
    @Json(name = "body")
    override var body: String? = "",
    @Json(name = "created_utc")
    override var createdUtc: Double = 0.0,
    @Json(name = "id")
    override var id: String = "",
    @Json(name = "likes")
    override var likes: Boolean? = null,
    @Json(name = "replies")
    var replies: Replies? = null,
    @Json(name = "saved")
    override var saved: Boolean = false,
    @Json(name = "score")
    override var score: Int = 0,
    @Json(name = "score_hidden")
    override var scoreHidden: Boolean? = false,
    @Json(name = "title")
    override var title: String? = "",
) : Node