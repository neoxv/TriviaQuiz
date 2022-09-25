package com.example.triviaquiz.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Insert
    suspend fun insertPlayer(player:Player)

    @Query("SELECT * FROM player_table")
    fun getAllPlayers(): Flow<List<Player>>

    @Update
    suspend fun updatePlayer(player: Player)

    @Delete
    suspend fun deletePlayer(player:Player)

    @Query("Select * From player_table where username = :name")
    fun getByName(name: String):Flow<Player>

}