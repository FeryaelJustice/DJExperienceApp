package com.feryaeldev.djexperience.data.model.domain

import com.google.gson.annotations.SerializedName
import java.util.*

data class New(
    @SerializedName("sourceId") val sourceId: String?,
    @SerializedName("sourceName") val sourceName: String?,
    @SerializedName("author") val author: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("urlToImage") val urlToImage: String?,
    @SerializedName("publishedAt") val publishedAt: Date?,
    @SerializedName("content") val content: String?
)
