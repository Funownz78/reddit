package ru.fwnz.humblr.data.dto.commentsByLink

import android.util.Log
import com.squareup.moshi.*
import kotlin.math.log

//@JsonQualifier
//@Retention(AnnotationRetention.RUNTIME)
//@Target(AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
//annotation class EmptyStringAsNull

class RepliesAdapter : JsonAdapter<Replies?>() {
    @FromJson
//    @EmptyStringAsNull
    override fun fromJson(reader: JsonReader): Replies? {
        val t = reader.peek()
        val v = reader.readJsonValue()
        Log.d("RepliesAdapter", "fromJson: $t")
        Log.d("RepliesAdapter", "fromJson: $v")
        Log.d("RepliesAdapter", "fromJson: ${t == JsonReader.Token.BEGIN_OBJECT}")
        return when (t) {
            JsonReader.Token.BEGIN_OBJECT -> v as Replies
            else -> null
        }
    }
    @ToJson
//    @EmptyStringAsNull
    override fun toJson(writer: JsonWriter, @Suppress("UNUSED_PARAMETER") value: Replies?) { }
}