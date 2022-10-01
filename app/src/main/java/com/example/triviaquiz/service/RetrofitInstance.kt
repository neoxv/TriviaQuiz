package com.example.triviaquiz.service

import com.example.triviaquiz.BuildConfig.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitInstance {
    companion object {
        private val interceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }

        private val client = OkHttpClient.Builder().apply {
            this.addInterceptor(interceptor)
            this.connectTimeout(10, TimeUnit.SECONDS)
            this.writeTimeout(10, TimeUnit.SECONDS)
            this.readTimeout(10, TimeUnit.SECONDS)
        }.build()

        fun getRetrofitInstance(): Retrofit {
            return Retrofit.Builder().baseUrl(BASE_URL).client(client)
                .addConverterFactory(MoshiConverterFactory.create()).build()
        }
    }
}