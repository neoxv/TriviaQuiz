package com.example.triviaquiz.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Question::class,Player::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TriviaQuizDatabase: RoomDatabase() {
    abstract fun playerDao():PlayerDao
    abstract fun questionDao():QuestionDao

    companion object{
        @Volatile
        private var INSTANCE: TriviaQuizDatabase? = null
        fun getInstance(context: Context): TriviaQuizDatabase{
            synchronized(this){
                var instance = INSTANCE
                if(instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        TriviaQuizDatabase::class.java,
                        "trivia_quiz_database"
                    ).build()
                }
                return instance
            }
        }
    }
}