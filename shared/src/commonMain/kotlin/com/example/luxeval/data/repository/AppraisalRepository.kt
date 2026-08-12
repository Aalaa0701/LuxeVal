package com.example.luxeval.data.repository

import com.example.luxeval.data.database.daos.AppraisalDao
import com.example.luxeval.data.mappers.toDomain
import com.example.luxeval.data.mappers.toEntity
import com.example.luxeval.domain.Appraisal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppraisalRepository(private val dao: AppraisalDao) {
    suspend fun save(appraisal: Appraisal) = dao.insert(appraisal.toEntity())
    suspend fun delete(appraisal: Appraisal) = dao.delete(appraisal.toEntity())
    fun observeForClient(clientId: String): Flow<List<Appraisal>> =
        dao.observeForClient(clientId).map { list -> list.map { it.toDomain() } }
    fun observeAll(): Flow<List<Appraisal>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }
    suspend fun getById(id: String): Appraisal? = dao.getById(id)?.toDomain()
}