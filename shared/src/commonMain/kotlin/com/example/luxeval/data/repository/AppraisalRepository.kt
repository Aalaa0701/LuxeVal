package com.example.luxeval.data.repository

import com.example.luxeval.data.database.daos.AppraisalDao
import com.example.luxeval.data.mappers.toDomain
import com.example.luxeval.data.mappers.toEntity
import com.example.luxeval.domain.Appraisal
import com.example.luxeval.domain.AppraisalPurpose
import com.example.luxeval.domain.AppraisalStatus
import com.example.luxeval.domain.DocumentIdGenerator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import kotlin.uuid.Uuid

class AppraisalRepository(private val dao: AppraisalDao) {
    suspend fun save(appraisal: Appraisal) = dao.insert(appraisal.toEntity())
    suspend fun delete(appraisal: Appraisal) = dao.delete(appraisal.toEntity())
    fun observeForClient(clientId: String): Flow<List<Appraisal>> =
        dao.observeForClient(clientId).map { list -> list.map { it.toDomain() } }
    fun observeAll(): Flow<List<Appraisal>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }
    suspend fun getById(id: String): Appraisal? = dao.getById(id)?.toDomain()
    fun observeById(id: String): Flow<Appraisal?> = dao.observeById(id).map { it?.toDomain() }
    suspend fun createDraft(): Appraisal {
        val now = Clock.System.now().toEpochMilliseconds()
        @OptIn(kotlin.uuid.ExperimentalUuidApi::class)
        val draft = Appraisal(
            id = Uuid.random().toString(),
            documentId = DocumentIdGenerator.next(),
            clientId = null,              // linked once the client form is filled/saved
            purpose = AppraisalPurpose.OTHER,  // user picks real purpose in the form
            status = AppraisalStatus.DRAFT,
            appraisalDate = now,
            createdAt = now,
            updatedAt = now
        )
        save(draft)
        return draft
    }
}