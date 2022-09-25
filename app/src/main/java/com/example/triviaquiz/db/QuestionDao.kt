package com.example.triviaquiz.db

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    @Insert
    suspend fun insertQuestion(question:Question)

    @Query("SELECT * FROM question_table")
    fun getQuestionByPlayer(): Flow<List<Question>>

    @Query("DELETE FROM question_table")
    suspend fun clearQuestions()
}