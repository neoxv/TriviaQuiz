package com.example.triviaquiz.room

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromString(value:String):List<String>{
        return value.split("##").toList()
    }

    @TypeConverter
    fun listToString(list:List<String>):String{
        return list.joinToString("##")
    }

}