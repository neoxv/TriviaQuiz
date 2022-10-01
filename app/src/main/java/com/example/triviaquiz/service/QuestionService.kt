package com.example.triviaquiz.service

import com.example.triviaquiz.model.Questions
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface QuestionService {
    @GET("/api.php")
    fun getQuestions(@Query("amount") amount: Int,@Query("difficulty") difficulty: String): Call<Questions>
}