package com.feryaeldev.djexperience.data.model.domain

import com.google.gson.annotations.SerializedName
import java.util.*

data class New(
    @SerializedName("sourceId") var sourceId: String?,
    @SerializedName("sourceName") var sourceName: String?,
    @SerializedName("author") var author: String?,
    @SerializedName("title") var title: String?,
    @SerializedName("description") var description: String?,
    @SerializedName("url") var url: String?,
    @SerializedName("urlToImage") var urlToImage: String?,
    @SerializedName("publishedAt") var publishedAt: Date?,
    @SerializedName("content") var content: String?
)
