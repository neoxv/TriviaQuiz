package com.example.triviaquiz

import android.app.Application
import com.example.triviaquiz.db.TriviaQuizDatabase

class TriviaQuizApplication: Application() {
    val database: TriviaQuizDatabase by lazy { TriviaQuizDatabase.getInstance(this)}
}