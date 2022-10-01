package com.example.triviaquiz.event

import com.example.triviaquiz.model.Question


interface QuestionCall {
        fun onSuccess(questionList: MutableList<Question>)
        fun onError(error: String)
}