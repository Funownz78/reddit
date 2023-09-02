package ru.fwnz.humblr.data.dto.aouth


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AOuthDto(
    @Json(name = "access_token")
    var accessToken: String = "",
    @Json(name = "expires_in")
    var expiresIn: Int = 0,
    @Json(name = "refresh_token")
    var refreshToken: String = "",
    @Json(name = "scope")
    var scope: String = "",
    @Json(name = "token_type")
    var tokenType: String = ""
)