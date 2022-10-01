package com.example.triviaquiz.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "question_table" )
data class Question(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id:Int,
    @ColumnInfo(name = "category")
    var category: String,
    @ColumnInfo(name = "correct_answer")
    var correct_answer: String,
    @ColumnInfo(name = "difficulty")
    var difficulty: String,
    @ColumnInfo(name = "incorrect_answers")
    var incorrect_answers: List<String>,
    @ColumnInfo(name = "question")
    var question: String,
    @ColumnInfo(name = "type")
    var type: String
)