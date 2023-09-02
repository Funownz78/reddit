package ru.fwnz.humblr.data.dto.commentsSaved


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ru.fwnz.humblr.domain.Comment

@JsonClass(generateAdapter = true)
data class DataX(
//    @Json(name = "all_awardings")
//    var allAwardings: List<Any> = listOf(),
//    @Json(name = "approved_at_utc")
//    var approvedAtUtc: Any? = Any(),
//    @Json(name = "approved_by")
//    var approvedBy: Any? = Any(),
//    @Json(name = "archived")
//    var archived: Boolean = false,
//    @Json(name = "associated_award")
//    var associatedAward: Any? = Any(),
//    @Json(name = "author")
//    var author: String = "",
//    @Json(name = "author_flair_background_color")
//    var authorFlairBackgroundColor: Any? = Any(),
//    @Json(name = "author_flair_css_class")
//    var authorFlairCssClass: Any? = Any(),
//    @Json(name = "author_flair_richtext")
//    var authorFlairRichtext: List<Any> = listOf(),
//    @Json(name = "author_flair_template_id")
//    var authorFlairTemplateId: Any? = Any(),
//    @Json(name = "author_flair_text")
//    var authorFlairText: Any? = Any(),
//    @Json(name = "author_flair_text_color")
//    var authorFlairTextColor: Any? = Any(),
//    @Json(name = "author_flair_type")
//    var authorFlairType: String = "",
//    @Json(name = "author_fullname")
//    var authorFullname: String = "",
//    @Json(name = "author_is_blocked")
//    var authorIsBlocked: Boolean = false,
//    @Json(name = "author_patreon_flair")
//    var authorPatreonFlair: Boolean = false,
//    @Json(name = "author_premium")
//    var authorPremium: Boolean = false,
//    @Json(name = "awarders")
//    var awarders: List<Any> = listOf(),
//    @Json(name = "banned_at_utc")
//    var bannedAtUtc: Any? = Any(),
//    @Json(name = "banned_by")
//    var bannedBy: Any? = Any(),
    @Json(name = "body")
    override var body: String = "",
//    @Json(name = "body_html")
//    var bodyHtml: String = "",
//    @Json(name = "can_gild")
//    var canGild: Boolean = false,
//    @Json(name = "can_mod_post")
//    var canModPost: Boolean = false,
//    @Json(name = "collapsed")
//    var collapsed: Boolean = false,
//    @Json(name = "collapsed_because_crowd_control")
//    var collapsedBecauseCrowdControl: Any? = Any(),
//    @Json(name = "collapsed_reason")
//    var collapsedReason: Any? = Any(),
//    @Json(name = "collapsed_reason_code")
//    var collapsedReasonCode: Any? = Any(),
//    @Json(name = "comment_type")
//    var commentType: Any? = Any(),
//    @Json(name = "controversiality")
//    var controversiality: Int = 0,
//    @Json(name = "created")
//    var created: Double = 0.0,
//    @Json(name = "created_utc")
//    var createdUtc: Double = 0.0,
//    @Json(name = "distinguished")
//    var distinguished: Any? = Any(),
//    @Json(name = "downs")
//    var downs: Int = 0,
//    @Json(name = "edited")
//    var edited: Boolean = false,
//    @Json(name = "gilded")
//    var gilded: Int = 0,
//    @Json(name = "gildings")
//    var gildings: Gildings? = Gildings(),
//    @Json(name = "id")
//    var id: String = "",
//    @Json(name = "is_submitter")
//    var isSubmitter: Boolean = false,
    @Json(name = "likes")
    override var likes: Boolean? = null,
//    @Json(name = "link_author")
//    var linkAuthor: String = "",
//    @Json(name = "link_id")
//    var linkId: String = "",
//    @Json(name = "link_permalink")
//    var linkPermalink: String = "",
//    @Json(name = "link_title")
//    var linkTitle: String = "",
//    @Json(name = "link_url")
//    var linkUrl: String = "",
//    @Json(name = "locked")
//    var locked: Boolean = false,
//    @Json(name = "mod_note")
//    var modNote: Any? = Any(),
//    @Json(name = "mod_reason_by")
//    var modReasonBy: Any? = Any(),
//    @Json(name = "mod_reason_title")
//    var modReasonTitle: Any? = Any(),
//    @Json(name = "mod_reports")
//    var modReports: List<Any> = listOf(),
    @Json(name = "name")
    override var name: String = "",
//    @Json(name = "no_follow")
//    var noFollow: Boolean = false,
//    @Json(name = "num_comments")
//    var numComments: Int = 0,
//    @Json(name = "num_reports")
//    var numReports: Any? = Any(),
//    @Json(name = "over_18")
//    var over18: Boolean = false,
//    @Json(name = "parent_id")
//    var parentId: String = "",
//    @Json(name = "permalink")
//    var permalink: String = "",
//    @Json(name = "quarantine")
//    var quarantine: Boolean = false,
//    @Json(name = "removal_reason")
//    var removalReason: Any? = Any(),
//    @Json(name = "replies")
//    var replies: String = "",
//    @Json(name = "report_reasons")
//    var reportReasons: Any? = Any(),
//    @Json(name = "saved")
//    var saved: Boolean = false,
//    @Json(name = "score")
//    var score: Int = 0,
//    @Json(name = "score_hidden")
//    var scoreHidden: Boolean = false,
//    @Json(name = "send_replies")
//    var sendReplies: Boolean = false,
//    @Json(name = "stickied")
//    var stickied: Boolean = false,
//    @Json(name = "subreddit")
//    var subreddit: String = "",
//    @Json(name = "subreddit_id")
//    var subredditId: String = "",
//    @Json(name = "subreddit_name_prefixed")
//    var subredditNamePrefixed: String = "",
//    @Json(name = "subreddit_type")
//    var subredditType: String = "",
//    @Json(name = "top_awarded_type")
//    var topAwardedType: Any? = Any(),
//    @Json(name = "total_awards_received")
//    var totalAwardsReceived: Int = 0,
//    @Json(name = "treatment_tags")
//    var treatmentTags: List<Any> = listOf(),
//    @Json(name = "unrepliable_reason")
//    var unrepliableReason: Any? = Any(),
//    @Json(name = "ups")
//    var ups: Int = 0,
//    @Json(name = "user_reports")
//    var userReports: List<Any> = listOf()
) : Comment