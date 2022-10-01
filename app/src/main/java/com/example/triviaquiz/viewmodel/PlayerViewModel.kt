package com.example.triviaquiz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triviaquiz.room.entity.Player
import com.example.triviaquiz.room.dao.PlayerDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PlayerViewModel(private val dao: PlayerDao): ViewModel() {
    fun insertPlayer(player: Player)=viewModelScope.launch {
        dao.insertPlayer(player)
    }

    fun updatePlayer(player: Player) = viewModelScope.launch {
        dao.updatePlayer(player)
    }

    fun getPlayers(): Flow<List<Player>> = dao.getAllPlayers()

    fun getPlayerByName(name:String): Flow<Player> =  dao.getByName(name)

    fun getHighScorePlayer(): Flow<Player> = dao.getHighScorePlayer()
}