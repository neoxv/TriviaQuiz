package com.example.triviaquiz.db

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

//    @Query("Select * From player_table where  high_score = (select  max(high_score) where high_level = (select max(high_level) from player_table))")
    @Query("Select * from player_table order By high_level DEsc, high_score Desc limit 1")
    fun getHighScorePlayer():Flow<Player>

}