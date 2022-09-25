package com.example.triviaquiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triviaquiz.db.Player
import com.example.triviaquiz.db.PlayerDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PlayerViewModel(private val dao: PlayerDao): ViewModel() {
    fun insertPlayer(player:Player)=viewModelScope.launch {
        dao.insertPlayer(player)
    }

    fun updatePlayer(player:Player) = viewModelScope.launch {
        dao.updatePlayer(player)
    }

    fun getPlayers()=viewModelScope.launch {
        dao.getAllPlayers()
    }

    fun getPlayerByName(name:String): Flow<Player> =  dao.getByName(name)

}