package com.example.luxeval.data.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.luxeval.data.database.entities.ClientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(client: ClientEntity)

    @Update
    suspend fun update(client: ClientEntity)

    @Delete
    suspend fun delete(client: ClientEntity)

    @Query("SELECT * FROM clients ORDER BY name ASC")
    fun observeClients(): Flow<List<ClientEntity>>

    @Query("SELECT * FROM clients WHERE id = :clientId LIMIT 1")
    suspend fun getById(clientId: String): ClientEntity?

    @Query("DELETE FROM clients WHERE id = :clientId")
    suspend fun deleteById(clientId: String)
}