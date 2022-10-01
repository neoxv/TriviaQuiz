package com.example.triviaquiz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triviaquiz.model.Question
import com.example.triviaquiz.room.dao.QuestionDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class QuestionViewModel(private val dao: QuestionDao): ViewModel() {
    fun getQuestions(): Flow<MutableList<Question>> = dao.getQuestionByPlayer()

    fun insertQuestion(question: Question)=viewModelScope.launch {
        dao.insertQuestion(question)
    }

    fun clearQuestions()=viewModelScope.launch {
        dao.clearQuestions()
    }
}