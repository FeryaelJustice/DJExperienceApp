package com.feryaeldev.djexperience.data.provider.services

import com.feryaeldev.djexperience.data.provider.services.newsapi.NewsApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun getRetrofit(serviceType: ServiceType): Retrofit {
    return when (serviceType) {
        ServiceType.NEWSAPI -> Retrofit.Builder().baseUrl(NewsApi.URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        ServiceType.UNKNOWN -> Retrofit.Builder().baseUrl("")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}