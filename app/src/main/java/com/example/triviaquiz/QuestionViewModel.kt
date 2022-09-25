package com.example.triviaquiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triviaquiz.db.Question
import com.example.triviaquiz.db.QuestionDao
import com.example.triviaquiz.db.Questions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class QuestionViewModel(private val dao: QuestionDao): ViewModel() {
    fun getQuestions(): Flow<List<Question>> = dao.getQuestionByPlayer()

    fun insertQuestion(question: Question)=viewModelScope.launch {
        dao.insertQuestion(question)
    }

    fun clearQuestions()=viewModelScope.launch {
        dao.clearQuestions()
    }
}