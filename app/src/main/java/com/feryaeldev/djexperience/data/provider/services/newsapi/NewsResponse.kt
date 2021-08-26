package com.feryaeldev.djexperience.data.provider.services.newsapi

import com.feryaeldev.djexperience.data.model.domain.New

data class NewsResponse(val status: String?, val totalResults: Int?, val articles: MutableList<New>?)
