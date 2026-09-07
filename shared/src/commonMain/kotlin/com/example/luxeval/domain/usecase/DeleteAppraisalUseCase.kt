package com.example.luxeval.domain.usecase

import com.example.luxeval.data.repository.AppraisalRepository
import com.example.luxeval.data.repository.ItemRepository
import com.example.luxeval.data.repository.SyncRepository

/**
 * Owns the full "delete an appraisal" sequence. AppraisalEntity has no
 * database-level foreign key to ItemEntity — nothing at the Room level
 * cascades this automatically. Every call site that deletes an appraisal
 * MUST go through this use case, never AppraisalRepository.delete()
 * directly, or items are orphaned locally and their photos leak in
 * Supabase Storage forever.
 */
class DeleteAppraisalUseCase(
    private val appraisalRepository: AppraisalRepository,
    private val itemRepository: ItemRepository,
    private val syncRepository: SyncRepository
) {
    suspend operator fun invoke(appraisalId: String) {
        val items = itemRepository.getItemsForAppraisal(appraisalId)
        items.forEach { item ->
            itemRepository.deleteItem(item.id)          // now also cleans remote photos
            syncRepository.deleteItemRemoteAsync(item.id)
        }
        appraisalRepository.getById(appraisalId)?.let { appraisalRepository.delete(it) }
        syncRepository.deleteAppraisalRemoteAsync(appraisalId)
    }
}