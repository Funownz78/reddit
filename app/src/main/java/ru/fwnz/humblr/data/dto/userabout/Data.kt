package ru.fwnz.humblr.data.dto.userabout


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ru.fwnz.humblr.domain.UserInfo

@JsonClass(generateAdapter = true)
data class Data(
//    @Json(name = "accept_chats")
//    var acceptChats: Boolean = false,
//    @Json(name = "accept_followers")
//    var acceptFollowers: Boolean = false,
//    @Json(name = "accept_pms")
//    var acceptPms: Boolean = false,
//    @Json(name = "awardee_karma")
//    var awardeeKarma: Int = 0,
//    @Json(name = "awarder_karma")
//    var awarderKarma: Int = 0,
//    @Json(name = "comment_karma")
//    var commentKarma: Int = 0,
//    @Json(name = "created")
//    var created: Double = 0.0,
//    @Json(name = "created_utc")
//    var createdUtc: Double = 0.0,
//    @Json(name = "has_subscribed")
//    var hasSubscribed: Boolean = false,
//    @Json(name = "has_verified_email")
//    var hasVerifiedEmail: Boolean = false,
//    @Json(name = "hide_from_robots")
//    var hideFromRobots: Boolean = false,
//    @Json(name = "icon_img")
//    override var iconImg: String = "",
//    @Json(name = "id")
//    var id: String = "",
//    @Json(name = "is_blocked")
//    var isBlocked: Boolean = false,
//    @Json(name = "is_employee")
//    var isEmployee: Boolean = false,
//    @Json(name = "is_friend")
//    var isFriend: Boolean = false,
//    @Json(name = "is_gold")
//    var isGold: Boolean = false,
//    @Json(name = "is_mod")
//    var isMod: Boolean = false,
//    @Json(name = "link_karma")
//    var linkKarma: Int = 0,
    @Json(name = "name")
    override var name: String = "",
//    @Json(name = "pref_show_snoovatar")
//    var prefShowSnoovatar: Boolean = false,
    @Json(name = "snoovatar_img")
    var snoovatarImg: String = "",
//    @Json(name = "snoovatar_size")
//    var snoovatarSize: Any? = Any(),
    @Json(name = "subreddit")
    var subreddit: Subreddit = Subreddit(),
//    @Json(name = "total_karma")
//    var totalKarma: Int = 0,
//    @Json(name = "verified")
//    var verified: Boolean = false
) : UserInfo {
    override val link: String get() = subreddit.displayNamePrefixed
    override var userIsSubscriber: Boolean get() = subreddit.userIsSubscriber
        set(value) { subreddit.userIsSubscriber = value }
    override val subredditDisplayName: String get() = subreddit.displayNamePrefixed
    override val iconImg: String get() = snoovatarImg

}