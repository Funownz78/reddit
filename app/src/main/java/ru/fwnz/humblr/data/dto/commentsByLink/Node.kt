package ru.fwnz.humblr.data.dto.commentsByLink


interface Node {
    var author: String
    var body: String?
    var createdUtc: Double
    var id: String
    var likes: Boolean?
    var saved: Boolean
    var score: Int
    var scoreHidden: Boolean?
    var title: String?
}