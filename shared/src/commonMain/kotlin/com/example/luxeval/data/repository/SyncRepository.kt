package com.example.luxeval.data.repository


import com.example.luxeval.data.remote.RemoteAppraisal
import com.example.luxeval.data.remote.RemoteClient
import com.example.luxeval.data.remote.RemoteItem
import com.example.luxeval.data.remote.detailsToJson
import com.example.luxeval.data.remote.toDomain
import com.example.luxeval.domain.*
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Best-effort remote sync. Room via the existing repositories stays the
 * source of truth the UI reads from. Every push here is fired *after* a
 * successful local write and never blocks it or surfaces its own errors to
 * the UI — a failed push (offline, etc.) doesn't lose data since it's already
 * safely in Room; it just won't be backed up until the next successful push
 * or the next full pullAll().
 */
class SyncRepository(
    private val supabase: SupabaseClient,
    private val appraisalRepository: AppraisalRepository,
    private val clientRepository: ClientRepository,
    private val itemRepository: ItemRepository
) {
    private val syncScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private fun currentUserIdOrNull(): String? = supabase.auth.currentUserOrNull()?.id
    // ───────────────────────────── Push (create/update) ─────────────────────────

    fun pushClientAsync(client: Client) = syncScope.launch {
        val userId = currentUserIdOrNull() ?: return@launch
        runCatching {
            supabase.postgrest.from("clients").upsert(
                RemoteClient(
                    id = client.id,
                    name = client.name,
                    phone = client.phone,
                    email = client.email,
                    address = client.address,
                    userId = userId
                )
            )
        }
    }

    fun pushAppraisalAsync(appraisal: Appraisal) = syncScope.launch {
        val userId = currentUserIdOrNull() ?: return@launch
        runCatching {
            supabase.postgrest.from("appraisals").upsert(
                RemoteAppraisal(
                    id = appraisal.id,
                    documentId = appraisal.documentId,
                    clientId = appraisal.clientId,
                    title = appraisal.title,
                    purpose = appraisal.purpose.name,
                    status = appraisal.status.name,
                    internalNotes = appraisal.internalNotes,
                    appraisalDate = appraisal.appraisalDate,
                    createdAt = appraisal.createdAt,
                    updatedAt = appraisal.updatedAt,
                    userId = userId
                )
            )
        }
    }

    fun pushItemAsync(item: AppraisalItem) = syncScope.launch {
        val userId = currentUserIdOrNull() ?: return@launch
        runCatching {
            supabase.postgrest.from("items").upsert(
                RemoteItem(
                    id = item.id,
                    appraisalId = item.appraisalId,
                    itemName = item.itemName,
                    category = item.category.name,
                    photoUris = item.photoUris.joinToString("‖"),
                    condition = item.condition.name,
                    conditionNotes = item.conditionNotes,
                    grossWeightGrams = item.grossWeightGrams,
                    hallmarks = item.hallmarks,
                    estimatedValue = item.estimatedValue,
                    currency = item.currency,
                    valuationBasis = item.valuationBasis.name,
                    valueSource = item.valueSource.name,
                    notes = item.notes,
                    createdAt = item.createdAt,
                    updatedAt = item.updatedAt,
                    details = item.detailsToJson(),
                    userId = userId
                )
            )
        }
    }

    // ───────────────────────────── Push (delete) ────────────────────────────────

    fun deleteClientRemoteAsync(clientId: String) = syncScope.launch {
        runCatching {
            supabase.postgrest.from("clients").delete { filter { eq("id", clientId) } }
        }
    }

    fun deleteAppraisalRemoteAsync(appraisalId: String) = syncScope.launch {
        runCatching {
            supabase.postgrest.from("appraisals").delete { filter { eq("id", appraisalId) } }
        }
    }

    fun deleteItemRemoteAsync(itemId: String) = syncScope.launch {
        runCatching {
            supabase.postgrest.from("items").delete { filter { eq("id", itemId) } }
        }
    }

    // ───────────────────────────── Pull (login / manual refresh) ────────────────

    /** Pulls every row belonging to the current user (RLS handles the filter)
     *  and merges into Room. Room's @Insert(onConflict = REPLACE) on primary
     *  key makes this idempotent: re-running it is always safe.
     *  Order matters: clients before appraisals (FK), appraisals before items
     *  isn't strictly required by a DB constraint here but keeps the data
     *  internally consistent as it lands locally. */
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

        val remoteItems = supabase.postgrest.from("items")
            .select(Columns.ALL)
            .decodeList<RemoteItem>()
        remoteItems.forEach { ri ->
            ri.toDomain()?.let { itemRepository.saveItem(it) }
            // null means OTHER category or an unrecognized row — silently
            // skipped rather than crashing the whole pull over one row.
        }
    }
}