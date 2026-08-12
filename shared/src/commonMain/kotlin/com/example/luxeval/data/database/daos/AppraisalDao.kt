package com.example.luxeval.data.database.daos

import androidx.room.*
import com.example.luxeval.data.database.entities.AppraisalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppraisalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(appraisal: AppraisalEntity)

    @Update
    suspend fun update(appraisal: AppraisalEntity)

    @Delete
    suspend fun delete(appraisal: AppraisalEntity)

    @Query("SELECT * FROM appraisals WHERE clientId = :clientId ORDER BY appraisalDate DESC")
    fun observeForClient(clientId: String): Flow<List<AppraisalEntity>>

    @Query("SELECT * FROM appraisals ORDER BY appraisalDate DESC")
    fun observeAll(): Flow<List<AppraisalEntity>>

    @Query("SELECT * FROM appraisals WHERE id = :id")
    suspend fun getById(id: String): AppraisalEntity?
}