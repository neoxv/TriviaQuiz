package com.example.triviaquiz.model

data class Questions(
    val response_code: Int,
    val results: List<Question>
)