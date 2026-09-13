// data/repository/SyncRepository.kt
package com.example.luxeval.data.repository

import com.example.luxeval.data.database.daos.SyncOutboxDao
import com.example.luxeval.data.database.entities.SyncOutboxEntity
import com.example.luxeval.data.network.ConnectionStatus
import com.example.luxeval.data.network.ConnectivityObserver
import com.example.luxeval.data.remote.RemoteAppraisal
import com.example.luxeval.data.remote.RemoteClient
import com.example.luxeval.data.remote.RemoteItem
import com.example.luxeval.data.remote.detailsToJson
import com.example.luxeval.data.remote.toDomain
import com.example.luxeval.data.remote.toRemoteRow
import com.example.luxeval.domain.*
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Clock

class SyncRepository(
    private val supabase: SupabaseClient,
    private val appraisalRepository: AppraisalRepository,
    private val clientRepository: ClientRepository,
    private val itemRepository: ItemRepository,
    private val outboxDao: SyncOutboxDao,
    connectivityObserver: ConnectivityObserver
) {
    private val syncScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private fun currentUserIdOrNull(): String? = supabase.auth.currentUserOrNull()?.id

    init {
        // Drain the outbox automatically the moment connectivity returns.
        connectivityObserver.observe()
            .filter { it == ConnectionStatus.AVAILABLE }
            .onEach {
                retryPendingPhotoUploads()
                retryOutbox()
            }
            .launchIn(syncScope)
    }

    // ───────────────────────────── Push (create/update) ─────────────────────────

    fun pushClientAsync(client: Client) = syncScope.launch {
        val userId = currentUserIdOrNull() ?: return@launch
        runCatching {
            supabase.postgrest.from("clients").upsert(client.toRemoteRow(userId))
        }.onFailure { e ->
            enqueueFailure(entityType = "client", entityId = client.id, operation = "upsert", error = e.message)
        }
    }

    fun pushAppraisalAsync(appraisal: Appraisal) = syncScope.launch {
        val userId = currentUserIdOrNull() ?: return@launch
        runCatching {
            supabase.postgrest.from("appraisals").upsert(appraisal.toRemoteRow(userId))
        }.onFailure { e ->
            enqueueFailure(entityType = "appraisal", entityId = appraisal.id, operation = "upsert", error = e.message)
        }
    }

    fun pushItemAsync(item: AppraisalItem) = syncScope.launch {
        val userId = currentUserIdOrNull() ?: return@launch
        runCatching {
            supabase.postgrest.from("items").upsert(item.toRemoteRow(userId))
        }.onFailure { e ->
            enqueueFailure(entityType = "item", entityId = item.id, operation = "upsert", error = e.message)
        }
    }

    // ───────────────────────────── Push (delete) ────────────────────────────────

    fun deleteClientRemoteAsync(clientId: String) = syncScope.launch {
        runCatching {
            supabase.postgrest.from("clients").delete { filter { eq("id", clientId) } }
        }.onFailure { e ->
            enqueueFailure(entityType = "client", entityId = clientId, operation = "delete", error = e.message)
        }
    }

    fun deleteAppraisalRemoteAsync(appraisalId: String) = syncScope.launch {
        runCatching {
            supabase.postgrest.from("appraisals").delete { filter { eq("id", appraisalId) } }
        }.onFailure { e ->
            enqueueFailure(entityType = "appraisal", entityId = appraisalId, operation = "delete", error = e.message)
        }
    }

    fun deleteItemRemoteAsync(itemId: String) = syncScope.launch {
        runCatching {
            supabase.postgrest.from("items").delete { filter { eq("id", itemId) } }
        }.onFailure { e ->
            enqueueFailure(entityType = "item", entityId = itemId, operation = "delete", error = e.message)
        }
    }

    private suspend fun enqueueFailure(entityType: String, entityId: String, operation: String, error: String?) {
        outboxDao.enqueue(
            SyncOutboxEntity(
                id = "$entityType-$operation-$entityId",   // deterministic — re-enqueuing the same failure just replaces the row
                entityType = entityType,
                entityId = entityId,
                operation = operation,
                lastError = error,
                createdAt = Clock.System.now().toEpochMilliseconds()
            )
        )
    }

    // ───────────────────────────── Outbox retry ──────────────────────────────────

    /** Walks every pending outbox entry oldest-first and retries it against
     *  Supabase. Successes are removed; failures stay queued with an
     *  incremented attempt count so future retries (next app start, next
     *  connectivity change, next pull-to-refresh) pick them up again. A
     *  missing local row (e.g. the item was deleted locally after the failed
     *  push) is treated as success — there's nothing left to push. */
    suspend fun retryOutbox() {
        val userId = currentUserIdOrNull() ?: return
        val pending = outboxDao.getAll()
        if (pending.isEmpty()) return

        for (entry in pending) {
            val result = runCatching {
                when (entry.entityType) {
                    "client" -> when (entry.operation) {
                        "upsert" -> {
                            val client = clientRepository.getClient(entry.entityId)
                            if (client != null) {
                                supabase.postgrest.from("clients").upsert(client.toRemoteRow(userId))
                            }
                        }
                        "delete" -> supabase.postgrest.from("clients").delete { filter { eq("id", entry.entityId) } }
                        else -> Unit
                    }

                    "appraisal" -> when (entry.operation) {
                        "upsert" -> {
                            val appraisal = appraisalRepository.getById(entry.entityId)
                            if (appraisal != null) {
                                supabase.postgrest.from("appraisals").upsert(appraisal.toRemoteRow(userId))
                            }
                        }
                        "delete" -> supabase.postgrest.from("appraisals").delete { filter { eq("id", entry.entityId) } }
                        else -> Unit
                    }

                    "item" -> when (entry.operation) {
                        "upsert" -> {
                            val item = itemRepository.getItemById(entry.entityId)
                            if (item != null) {
                                supabase.postgrest.from("items").upsert(item.toRemoteRow(userId))
                            }
                        }
                        "delete" -> supabase.postgrest.from("items").delete { filter { eq("id", entry.entityId) } }
                        else -> Unit
                    }

                    else -> Unit
                }
            }

            if (result.isSuccess) {
                outboxDao.remove(entry.id)
            } else {
                outboxDao.recordFailure(entry.id, result.exceptionOrNull()?.message ?: "retry failed")
            }
        }
    }
    suspend fun retryPendingPhotoUploads() {
        val resolvedItemIds = itemRepository.retryPendingPhotoUploads()
        resolvedItemIds.forEach { itemId ->
            itemRepository.getItemById(itemId)?.let { pushItemAsync(it) }
        }
    }

    // ───────────────────────────── Pull (login / manual refresh) ────────────────
    // unchanged from your existing pullAll() — kept exactly as-is below

    suspend fun pullAll(): Result<Unit> = runCatching {
        val remoteClients = supabase.postgrest.from("clients")
            .select(Columns.ALL)
            .decodeList<RemoteClient>()
        remoteClients.forEach { rc ->
            clientRepository.saveClient(
                Client(id = rc.id, name = rc.name, phone = rc.phone, email = rc.email, address = rc.address)
            )
        }

        val remoteAppraisals = supabase.postgrest.from("appraisals")
            .select(Columns.ALL)
            .decodeList<RemoteAppraisal>()
        remoteAppraisals.forEach { ra ->
            appraisalRepository.save(
                Appraisal(
                    id = ra.id,
                    documentId = ra.documentId,
                    clientId = ra.clientId,
                    title = ra.title,
                    purpose = AppraisalPurpose.valueOf(ra.purpose),
                    status = AppraisalStatus.valueOf(ra.status),
                    internalNotes = ra.internalNotes,
                    appraisalDate = ra.appraisalDate,
                    createdAt = ra.createdAt,
                    updatedAt = ra.updatedAt
                )
            )
        }
        val remoteItems = supabase.postgrest.from("items").select(Columns.ALL).decodeList<RemoteItem>()
        remoteItems.forEach { ri ->
            ri.toDomain()?.let { itemRepository.applyRemoteItem(it) }   // CHANGED from saveItem(it)
        }
//        val remoteItems = supabase.postgrest.from("items")
//            .select(Columns.ALL)
//            .decodeList<RemoteItem>()
//        remoteItems.forEach { ri ->
//            ri.toDomain()?.let { itemRepository.applyRemoteItem(it) }   // was saveItem(it)
//        }
//        remoteItems.forEach { ri ->
//            ri.toDomain()?.let { itemRepository.saveItem(it) }
//        }
    }
}