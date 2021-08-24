package com.feryaeldev.djexperience.data.provider.services.newsapi

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    @GET("/everything")
    suspend fun get(@Query("q") q: String, @Query("apiKey") apiKey: String): Response<NewsResponse>
}