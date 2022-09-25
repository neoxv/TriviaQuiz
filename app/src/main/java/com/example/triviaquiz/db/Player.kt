package com.example.triviaquiz.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_table" )
data class Player(
    @ColumnInfo(name = "username")
    var username:String,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id:Int? = null,
    @ColumnInfo(name = "score")
    var score:Int = 0,
    @ColumnInfo(name = "high_score")
    var highScore:Int = 0,
    @ColumnInfo(name = "high_level")
    var highLevel:Int = 0
)
