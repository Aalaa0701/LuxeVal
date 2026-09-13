package com.example.luxeval.data.repository

import com.example.luxeval.data.database.AppDatabase
import com.example.luxeval.data.database.daos.ItemDao
import com.example.luxeval.data.database.daos.PendingPhotoUploadDao
import com.example.luxeval.data.database.entities.ItemEntity
import com.example.luxeval.data.database.entities.PendingPhotoUploadEntity
import com.example.luxeval.data.mappers.toDetailsEntity
import com.example.luxeval.data.mappers.toDomain
import com.example.luxeval.data.mappers.toItemEntity
import com.example.luxeval.domain.AppraisalItem
import com.example.luxeval.domain.ItemCategory
import com.example.luxeval.domain.ItemSummary
import com.example.luxeval.domain.withPhotoUris
import com.example.luxeval.presentation.photo.PhotoStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import androidx.room.useWriterConnection
import androidx.room.immediateTransaction
/**
 * This is the class that actually justifies the whole design: it's the one
 * place that knows how to fan a sealed AppraisalItem out into (base row +
 * category-specific row), and how to fan it back in when reading. Everything
 * above and below this class only ever deals with clean types — either plain
 * Room entities, or the fully-typed AppraisalItem sealed class. Nothing else
 * in the app needs to know two tables are involved.
 */
class ItemRepository(
    private val dao: ItemDao,
    private val photoStorage: PhotoStorage,
    private val photoRepository: PhotoRepository,
    private val pendingPhotoUploadDao: PendingPhotoUploadDao,
    private val database: AppDatabase,
) {
    suspend fun saveItem(item: AppraisalItem): AppraisalItem {
        val resolvedPhotoUris = item.photoUris.map { uri ->
            if (uri.startsWith("supabase://")) {
                uri
            } else {
                runCatching { photoRepository.uploadPhoto(item.id, uri) }
                    .onSuccess { pendingPhotoUploadDao.remove(pendingUploadId(item.id, uri)) }
                    .onFailure { e ->
                        pendingPhotoUploadDao.upsert(
                            PendingPhotoUploadEntity(
                                id = pendingUploadId(item.id, uri),
                                itemId = item.id,
                                localPath = uri,
                                createdAt = Clock.System.now().toEpochMilliseconds()
                            )
                        )
                        println("PHOTO UPLOAD: FAILED for $uri -> ${e.message}")
                    }
                    .getOrElse { uri }
            }
        }
        val resolvedItem = item.withPhotoUris(resolvedPhotoUris)
        database.useWriterConnection { transactor ->
            transactor.immediateTransaction {
                dao.insertItem(resolvedItem.toItemEntity())
                when (resolvedItem) {
                    is AppraisalItem.Ring -> dao.insertRingDetails(resolvedItem.toDetailsEntity())
                    is AppraisalItem.Necklace -> dao.insertNecklaceDetails(resolvedItem.toDetailsEntity())
                    is AppraisalItem.Pendant -> dao.insertPendantDetails(resolvedItem.toDetailsEntity())
                    is AppraisalItem.Bracelet -> dao.insertBraceletDetails(resolvedItem.toDetailsEntity())
                    is AppraisalItem.Earrings -> dao.insertEarringsDetails(resolvedItem.toDetailsEntity())
                    is AppraisalItem.Brooch -> dao.insertBroochDetails(resolvedItem.toDetailsEntity())
                    is AppraisalItem.Watch -> dao.insertWatchDetails(resolvedItem.toDetailsEntity())
                    is AppraisalItem.LooseGemstone -> dao.insertLooseGemstoneDetails(resolvedItem.toDetailsEntity())
                    is AppraisalItem.PearlJewelry -> dao.insertPearlDetails(resolvedItem.toDetailsEntity())
                    is AppraisalItem.Other -> throw NotImplementedError("No detail table wired up yet for ${resolvedItem.category}")
                }
            }
        }

        return resolvedItem
    }

//    suspend fun saveItem(item: AppraisalItem): AppraisalItem {
//        val resolvedPhotoUris = item.photoUris.map { uri ->
//            if (uri.startsWith("supabase://")) {
//                uri
//            } else {
//                runCatching { photoRepository.uploadPhoto(item.id, uri) }
//                    .onSuccess { pendingPhotoUploadDao.remove(pendingUploadId(item.id, uri)) }
//                    .onFailure { e ->
//                        pendingPhotoUploadDao.upsert(
//                            PendingPhotoUploadEntity(
//                                id = pendingUploadId(item.id, uri),
//                                itemId = item.id,
//                                localPath = uri,
//                                createdAt = Clock.System.now().toEpochMilliseconds()
//                            )
//                        )
//                        println("PHOTO UPLOAD: FAILED for $uri -> ${e.message}")
//                    }
//                    .getOrElse { uri }
//            }
//        }
//        val resolvedItem = item.withPhotoUris(resolvedPhotoUris)
//
//        dao.insertItem(resolvedItem.toItemEntity())
//        when (resolvedItem) {
//            is AppraisalItem.Ring -> dao.insertRingDetails(resolvedItem.toDetailsEntity())
//            is AppraisalItem.Necklace -> dao.insertNecklaceDetails(resolvedItem.toDetailsEntity())
//            is AppraisalItem.Pendant -> dao.insertPendantDetails(resolvedItem.toDetailsEntity())
//            is AppraisalItem.Bracelet -> dao.insertBraceletDetails(resolvedItem.toDetailsEntity())
//            is AppraisalItem.Earrings -> dao.insertEarringsDetails(resolvedItem.toDetailsEntity())
//            is AppraisalItem.Brooch -> dao.insertBroochDetails(resolvedItem.toDetailsEntity())
//            is AppraisalItem.Watch -> dao.insertWatchDetails(resolvedItem.toDetailsEntity())
//            is AppraisalItem.LooseGemstone -> dao.insertLooseGemstoneDetails(resolvedItem.toDetailsEntity())
//            is AppraisalItem.PearlJewelry -> dao.insertPearlDetails(resolvedItem.toDetailsEntity())
//            is AppraisalItem.Other -> throw NotImplementedError("No detail table wired up yet for ${resolvedItem.category}")
//        }
//        return resolvedItem
//    }

    /** Used only when applying data that came FROM the remote (pull/sync). */
    suspend fun applyRemoteItem(item: AppraisalItem) {
        dao.insertItem(item.toItemEntity())
        when (item) {
            is AppraisalItem.Ring -> dao.insertRingDetails(item.toDetailsEntity())
            is AppraisalItem.Necklace -> dao.insertNecklaceDetails(item.toDetailsEntity())
            is AppraisalItem.Pendant -> dao.insertPendantDetails(item.toDetailsEntity())
            is AppraisalItem.Bracelet -> dao.insertBraceletDetails(item.toDetailsEntity())
            is AppraisalItem.Earrings -> dao.insertEarringsDetails(item.toDetailsEntity())
            is AppraisalItem.Brooch -> dao.insertBroochDetails(item.toDetailsEntity())
            is AppraisalItem.Watch -> dao.insertWatchDetails(item.toDetailsEntity())
            is AppraisalItem.LooseGemstone -> dao.insertLooseGemstoneDetails(item.toDetailsEntity())
            is AppraisalItem.PearlJewelry -> dao.insertPearlDetails(item.toDetailsEntity())
            is AppraisalItem.Other -> Unit
        }
    }

    /** Retries every queued photo upload. Returns the itemIds that changed,
     *  so the caller (SyncRepository) knows which rows need re-pushing. */
    suspend fun retryPendingPhotoUploads(): Set<String> {
        val resolvedItemIds = mutableSetOf<String>()
        for (entry in pendingPhotoUploadDao.getAll()) {
            runCatching { photoRepository.uploadPhoto(entry.itemId, entry.localPath) }
                .onSuccess { remoteUri ->
                    replacePhotoUri(entry.itemId, oldUri = entry.localPath, newUri = remoteUri)
                    pendingPhotoUploadDao.remove(entry.id)
                    resolvedItemIds += entry.itemId
                }
                .onFailure { e -> pendingPhotoUploadDao.recordFailure(entry.id, e.message) }
        }
        return resolvedItemIds
    }

    private suspend fun replacePhotoUri(itemId: String, oldUri: String, newUri: String) {
        val entity = dao.getItemById(itemId) ?: return
        dao.insertItem(entity.copy(photoUris = entity.photoUris.map { if (it == oldUri) newUri else it }))
    }

    private fun pendingUploadId(itemId: String, localPath: String) =
        "photo-${com.example.luxeval.presentation.photo.cacheKeyFor(itemId + localPath)}"

    suspend fun deleteItem(itemId: String) {
        val entity = dao.getItemById(itemId)
        dao.deleteItem(itemId)
        pendingPhotoUploadDao.removeAllForItem(itemId)   // NEW — don't retry uploads for a deleted item
        entity?.photoUris?.forEach { uri ->
            if (uri.startsWith("supabase://")) photoRepository.deletePhoto(uri)
            else photoStorage.deleteImage(uri)
        }
    }
//    suspend fun saveItem(item: AppraisalItem): AppraisalItem {
////        val resolvedPhotoUris = item.photoUris.map { uri ->
////            if (uri.startsWith("supabase://")) {
////                uri // already uploaded (e.g. editing an existing item, didn't touch photos)
////            } else {
////                runCatching { photoRepository.uploadPhoto(item.id, uri) }
////                    .getOrElse { uri } // upload failed (offline?) — keep local path, retry on next save
////            }
////        }
//        val resolvedPhotoUris = item.photoUris.map { uri ->
//            if (uri.startsWith("supabase://")) {
//                uri
//            } else {
//                runCatching { photoRepository.uploadPhoto(item.id, uri) }
//                    .onSuccess { println("PHOTO UPLOAD: success -> $it") }
//                    .onFailure { println("PHOTO UPLOAD: FAILED for $uri -> ${it.message}"); it.printStackTrace() }
//                    .getOrElse { uri }
//            }
//        }
//        val resolvedItem = item.withPhotoUris(resolvedPhotoUris)
//
//        dao.insertItem(resolvedItem.toItemEntity())
//        when (resolvedItem) {
//            is AppraisalItem.Ring -> dao.insertRingDetails(resolvedItem.toDetailsEntity())
//            is AppraisalItem.Necklace -> dao.insertNecklaceDetails(resolvedItem.toDetailsEntity())
//            is AppraisalItem.Pendant -> dao.insertPendantDetails(resolvedItem.toDetailsEntity())
//            is AppraisalItem.Bracelet -> dao.insertBraceletDetails(resolvedItem.toDetailsEntity())
//            is AppraisalItem.Earrings -> dao.insertEarringsDetails(resolvedItem.toDetailsEntity())
//            is AppraisalItem.Brooch -> dao.insertBroochDetails(resolvedItem.toDetailsEntity())
//            is AppraisalItem.Watch -> dao.insertWatchDetails(resolvedItem.toDetailsEntity())
//            is AppraisalItem.LooseGemstone -> dao.insertLooseGemstoneDetails(resolvedItem.toDetailsEntity())
//            is AppraisalItem.PearlJewelry -> dao.insertPearlDetails(resolvedItem.toDetailsEntity())
//            is AppraisalItem.Other -> throw NotImplementedError("No detail table wired up yet for ${resolvedItem.category}")
//        }
//        return resolvedItem
//    }
    /** Used only when applying data that came FROM the remote (pull/sync).
     *  Never touches photoUris — the remote row is already the source of truth
     *  for whatever photos have successfully uploaded. */
//    suspend fun applyRemoteItem(item: AppraisalItem) {
//        dao.insertItem(item.toItemEntity())
//        when (item) {
//            is AppraisalItem.Ring -> dao.insertRingDetails(item.toDetailsEntity())
//            is AppraisalItem.Necklace -> dao.insertNecklaceDetails(item.toDetailsEntity())
//            is AppraisalItem.Pendant -> dao.insertPendantDetails(item.toDetailsEntity())
//            is AppraisalItem.Bracelet -> dao.insertBraceletDetails(item.toDetailsEntity())
//            is AppraisalItem.Earrings -> dao.insertEarringsDetails(item.toDetailsEntity())
//            is AppraisalItem.Brooch -> dao.insertBroochDetails(item.toDetailsEntity())
//            is AppraisalItem.Watch -> dao.insertWatchDetails(item.toDetailsEntity())
//            is AppraisalItem.LooseGemstone -> dao.insertLooseGemstoneDetails(item.toDetailsEntity())
//            is AppraisalItem.PearlJewelry -> dao.insertPearlDetails(item.toDetailsEntity())
//            is AppraisalItem.Other -> Unit
//        }
//    }


    /** One-shot read — simplest option, good enough for the appraisal-creation flow. */
    suspend fun getItemsForAppraisal(appraisalId: String): List<AppraisalItem> {
        return buildList {
            for (entity in dao.getItemsForAppraisalOnce(appraisalId)) {
                reconstruct(entity)?.let(::add)
            }
        }
    }
//    suspend fun deleteItem(itemId: String) {
//        val entity = dao.getItemById(itemId)
//        dao.deleteItem(itemId)
//        entity?.photoUris?.forEach { uri ->
//            if (uri.startsWith("supabase://")) {
//                photoRepository.deletePhoto(uri)   // remote — was missing before
//            } else {
//                photoStorage.deleteImage(uri)      // local — unchanged
//            }
//        }
//    }

    /**
     * Reactive read for the dashboard/appraisal-detail screen. Note this re-does the
     * Reactive read for the dashboard/appraisal-detail screen. Note this re-does the
     * detail-table lookups every time the base `items` table changes, which is fine
     * at appraisal-sized item counts (dozens, not thousands) — if that ever becomes
     * a bottleneck, switch to Room's @Relation/@Junction for a single joined query.
     */
    fun observeItemsForAppraisal(appraisalId: String): Flow<List<AppraisalItem>> =
        dao.observeItemsForAppraisal(appraisalId).map { entities ->
            // Flow.map's transform lambda is `suspend`, so calling the suspend
            // detail-table lookups below is fine directly, no extra wrapper needed.
            entities.mapNotNull { reconstruct(it) }
        }


    fun observeAllItemSummaries(): Flow<List<ItemSummary>> =
        dao.observeAllItems().map { entities ->
            entities.map {
                ItemSummary(
                    id = it.id,
                    appraisalId = it.appraisalId,
                    itemName = it.itemName,
                    category = ItemCategory.valueOf(it.category),
                    estimatedValue = it.estimatedValue,
                    currency = it.currency,
                    primaryPhotoUri = it.photoUris.firstOrNull(),
                    createdAt = it.createdAt
                )
            }
        }

    suspend fun getItemById(itemId: String): AppraisalItem? {
        val entity = dao.getItemById(itemId) ?: return null
        return reconstruct(entity)
    }

    private suspend fun reconstruct(entity: ItemEntity): AppraisalItem? =
        when (ItemCategory.valueOf(entity.category)) {
            ItemCategory.RING -> dao.getRingDetails(entity.id)?.toDomain(entity)
            ItemCategory.NECKLACE -> dao.getNecklaceDetails(entity.id)?.toDomain(entity)
            ItemCategory.PENDANT -> dao.getPendantDetails(entity.id)?.toDomain(entity)
            ItemCategory.BRACELET -> dao.getBraceletDetails(entity.id)?.toDomain(entity)
            ItemCategory.EARRINGS -> dao.getEarringsDetails(entity.id)?.toDomain(entity)
            ItemCategory.BROOCH -> dao.getBroochDetails(entity.id)?.toDomain(entity)
            ItemCategory.WATCH -> dao.getWatchDetails(entity.id)?.toDomain(entity)
            ItemCategory.LOOSE_GEMSTONE -> dao.getLooseGemstoneDetails(entity.id)?.toDomain(entity)
            ItemCategory.PEARL_JEWELRY -> dao.getPearlDetails(entity.id)?.toDomain(entity)
            ItemCategory.OTHER -> null
        }

    
    suspend fun getRing(itemId: String): AppraisalItem.Ring? {
        val entity = dao.getItemById(itemId) ?: return null
        return dao.getRingDetails(itemId)?.toDomain(entity)
    }

    suspend fun getNecklace(itemId: String): AppraisalItem.Necklace? {
        val entity = dao.getItemById(itemId) ?: return null
        return dao.getNecklaceDetails(itemId)?.toDomain(entity)
    }
    suspend fun getWatch(itemId: String): AppraisalItem.Watch? {
        val entity = dao.getItemById(itemId) ?: return null
        return dao.getWatchDetails(itemId)?.toDomain(entity)
    }

    suspend fun getPearlJewelry(itemId: String): AppraisalItem.PearlJewelry? {
        val entity = dao.getItemById(itemId) ?: return null
        return dao.getPearlDetails(itemId)?.toDomain(entity)
    }

    suspend fun getPendant(itemId: String): AppraisalItem.Pendant? {
        val entity = dao.getItemById(itemId) ?: return null
        return dao.getPendantDetails(itemId)?.toDomain(entity)
    }
    suspend fun getBracelet(itemId: String): AppraisalItem.Bracelet? {
        val entity = dao.getItemById(itemId) ?: return null
        return dao.getBraceletDetails(itemId)?.toDomain(entity)
    }
    suspend fun getEarrings(itemId: String): AppraisalItem.Earrings? {
        val entity = dao.getItemById(itemId) ?: return null
        return dao.getEarringsDetails(itemId)?.toDomain(entity)
    }
    suspend fun getBrooch(itemId: String): AppraisalItem.Brooch? {
        val entity = dao.getItemById(itemId) ?: return null
        return dao.getBroochDetails(itemId)?.toDomain(entity)
    }
    suspend fun getLooseGemstone(itemId: String): AppraisalItem.LooseGemstone? {
        val entity = dao.getItemById(itemId) ?: return null
        return dao.getLooseGemstoneDetails(itemId)?.toDomain(entity)
    }
}