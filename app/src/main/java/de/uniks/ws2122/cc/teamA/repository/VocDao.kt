package de.uniks.ws2122.cc.teamA.repository

import androidx.room.*
import de.uniks.ws2122.cc.teamA.model.User

@Dao
interface VocDao {
    @Insert
    suspend fun insertUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Update
    suspend fun updateUser(user: User)
}