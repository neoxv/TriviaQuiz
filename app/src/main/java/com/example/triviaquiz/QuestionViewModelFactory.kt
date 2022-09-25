package com.example.triviaquiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.triviaquiz.db.QuestionDao

class QuestionViewModelFactory(private val dao: QuestionDao): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(QuestionViewModel::class.java)){
            return QuestionViewModel(dao) as T
        }
        throw java.lang.IllegalArgumentException("Unknown View Model Class")
    }
}