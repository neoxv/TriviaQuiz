package com.example.triviaquiz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.triviaquiz.room.dao.QuestionDao

class QuestionViewModelFactory(private val dao: QuestionDao): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(QuestionViewModel::class.java)){
            return QuestionViewModel(dao) as T
        }
        throw java.lang.IllegalArgumentException("Unknown View Model Class")
    }
}