package com.example.triviaquiz

import com.example.triviaquiz.db.Question


interface QuestionCall {
        fun onSuccess(questionList: MutableList<Question>)
        fun onError(error: String)
}