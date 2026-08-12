package com.example.luxeval.data
import com.example.luxeval.data.database.daos.ItemDao
import com.example.luxeval.data.database.entities.ItemEntity
import com.example.luxeval.domain.AppraisalItem
import com.example.luxeval.domain.ItemCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * This is the class that actually justifies the whole design: it's the one
 * place that knows how to fan a sealed AppraisalItem out into (base row +
 * category-specific row), and how to fan it back in when reading. Everything
 * above and below this class only ever deals with clean types — either plain
 * Room entities, or the fully-typed AppraisalItem sealed class. Nothing else
 * in the app needs to know two tables are involved.
 */
class ItemRepository(private val dao: ItemDao) {

    suspend fun saveItem(item: AppraisalItem) {
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
            is AppraisalItem.Other -> throw NotImplementedError(
                "No detail table wired up yet for ${item.category}"
            )
        }
    }

    /** One-shot read — simplest option, good enough for the appraisal-creation flow. */
    suspend fun getItemsForAppraisal(appraisalId: String): List<AppraisalItem> {
        return buildList {
            for (entity in dao.getItemsForAppraisalOnce(appraisalId)) {
                reconstruct(entity)?.let(::add)
            }
        }
    }

    /**
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

    suspend fun deleteItem(itemId: String) = dao.deleteItem(itemId)

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
}
