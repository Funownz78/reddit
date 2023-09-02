package ru.fwnz.humblr.data.dto.userabout


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Subreddit(
//    @Json(name = "accept_followers")
//    var acceptFollowers: Boolean = false,
//    @Json(name = "allowed_media_in_comments")
//    var allowedMediaInComments: List<Any> = listOf(),
//    @Json(name = "banner_img")
//    var bannerImg: String = "",
//    @Json(name = "banner_size")
//    var bannerSize: Any? = Any(),
//    @Json(name = "community_icon")
//    var communityIcon: Any? = Any(),
//    @Json(name = "default_set")
//    var defaultSet: Boolean = false,
//    @Json(name = "description")
//    var description: String = "",
//    @Json(name = "disable_contributor_requests")
//    var disableContributorRequests: Boolean = false,
    @Json(name = "display_name")
    var displayName: String = "",
    @Json(name = "display_name_prefixed")
    var displayNamePrefixed: String = "",
//    @Json(name = "free_form_reports")
//    var freeFormReports: Boolean = false,
//    @Json(name = "header_img")
//    var headerImg: Any? = Any(),
//    @Json(name = "header_size")
//    var headerSize: Any? = Any(),
//    @Json(name = "icon_color")
//    var iconColor: String = "",
//    @Json(name = "icon_img")
//    var iconImg: String = "",
//    @Json(name = "icon_size")
//    var iconSize: List<Int> = listOf(),
//    @Json(name = "is_default_banner")
//    var isDefaultBanner: Boolean = false,
//    @Json(name = "is_default_icon")
//    var isDefaultIcon: Boolean = false,
//    @Json(name = "key_color")
//    var keyColor: String = "",
//    @Json(name = "link_flair_enabled")
//    var linkFlairEnabled: Boolean = false,
//    @Json(name = "link_flair_position")
//    var linkFlairPosition: String = "",
//    @Json(name = "name")
//    var name: String = "",
//    @Json(name = "over_18")
//    var over18: Boolean = false,
//    @Json(name = "previous_names")
//    var previousNames: List<Any> = listOf(),
//    @Json(name = "primary_color")
//    var primaryColor: String = "",
//    @Json(name = "public_description")
//    var publicDescription: String = "",
//    @Json(name = "quarantine")
//    var quarantine: Boolean = false,
//    @Json(name = "restrict_commenting")
//    var restrictCommenting: Boolean = false,
//    @Json(name = "restrict_posting")
//    var restrictPosting: Boolean = false,
//    @Json(name = "show_media")
//    var showMedia: Boolean = false,
//    @Json(name = "submit_link_label")
//    var submitLinkLabel: String = "",
//    @Json(name = "submit_text_label")
//    var submitTextLabel: String = "",
//    @Json(name = "subreddit_type")
//    var subredditType: String = "",
//    @Json(name = "subscribers")
//    var subscribers: Int = 0,
//    @Json(name = "title")
//    var title: String = "",
//    @Json(name = "url")
//    var url: String = "",
//    @Json(name = "user_is_banned")
//    var userIsBanned: Boolean = false,
//    @Json(name = "user_is_contributor")
//    var userIsContributor: Boolean = false,
//    @Json(name = "user_is_moderator")
//    var userIsModerator: Boolean = false,
//    @Json(name = "user_is_muted")
//    var userIsMuted: Any? = Any(),
    @Json(name = "user_is_subscriber")
    var userIsSubscriber: Boolean = false
)