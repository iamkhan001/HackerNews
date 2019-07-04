package com.nstudio.hackernews.model

import com.google.gson.annotations.SerializedName

data class Comment(
    @SerializedName("by") val by: String,
    @SerializedName("id") val id: Int,
    @SerializedName("kids") val kids: List<Int>,
    @SerializedName("parent") val parent: Int,
    @SerializedName("text") val text: String,
    @SerializedName("time") val time: Long,
    @SerializedName("type") val type: String,
    @SerializedName("deleted") val deleted: Boolean,
    @SerializedName("dead") val dead: Boolean
    )
