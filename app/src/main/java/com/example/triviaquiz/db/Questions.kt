package com.example.triviaquiz.db

data class Questions(
    val response_code: Int,
    val results: List<Question>
)