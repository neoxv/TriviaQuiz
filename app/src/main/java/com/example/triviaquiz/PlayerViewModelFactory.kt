package com.example.triviaquiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.triviaquiz.db.PlayerDao

class PlayerViewModelFactory(val dao: PlayerDao):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)){
            return  PlayerViewModel(dao) as T
        }
        throw  java.lang.IllegalArgumentException("Unknown ViewModel class")
    }
}