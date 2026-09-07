package com.example.luxeval.data.repository

import com.example.luxeval.data.AppSettingsStore
import com.example.luxeval.data.AppraiserProfileStore
import com.example.luxeval.data.MeasurementUnit
import com.example.luxeval.data.remote.RemoteBusinessProfile
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.time.Clock

/**
 * Backs up the business profile (name, license, address, phone, logo,
 * signature, currency, unit) to Supabase so a reinstall or a second device
 * doesn't lose it. Mirrors SyncRepository's fire-and-forget push pattern:
 * local Settings/AppraiserProfileStore stays the source of truth the UI
 * reads from; pushes here never block the local write or surface their own
 * errors — a failed push just means the backup is stale until the next
 * successful one.
 */
class BusinessProfileRepository(
    private val supabase: SupabaseClient,
    private val profileStore: AppraiserProfileStore,
    private val settingsStore: AppSettingsStore,
    private val photoRepository: PhotoRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    /** Uploads the logo, updates the local store with the resulting
     *  supabase:// key, and pushes the whole profile row. Returns the new
     *  key so the caller (ViewModel) can update UI state without waiting
     *  on a Flow re-emission. */
    suspend fun uploadLogo(localPath: String): String {
        val userId = requireNotNull(supabase.auth.currentUserOrNull()?.id) {
            "Cannot upload logo without an authenticated user"
        }
        val extension = localPath.substringAfterLast('.', "jpg")
        val uri = photoRepository.uploadFile(
            PhotoRepository.BUSINESS_ASSETS_BUCKET, "$userId/logo.$extension", localPath
        )
        profileStore.setLogoPath(uri)
        pushRemoteAsync()
        return uri
    }

    suspend fun uploadSignature(localPath: String): String {
        val userId = requireNotNull(supabase.auth.currentUserOrNull()?.id) {
            "Cannot upload signature without an authenticated user"
        }
        val extension = localPath.substringAfterLast('.', "jpg")
        val uri = photoRepository.uploadFile(
            PhotoRepository.BUSINESS_ASSETS_BUCKET, "$userId/signature.$extension", localPath
        )
        profileStore.setSignaturePath(uri)
        pushRemoteAsync()
        return uri
    }

    /** Public entry point for text-field changes (name/license/address/phone/
     *  currency/unit) — the ViewModel debounces calls to this so typing a
     *  business name doesn't fire a network request per keystroke. */
    fun pushFieldsAsync() = pushRemoteAsync()

    private fun pushRemoteAsync() = scope.launch {
        val userId = supabase.auth.currentUserOrNull()?.id ?: return@launch
        runCatching {
            supabase.postgrest.from("business_profiles").upsert(
                RemoteBusinessProfile(
                    userId = userId,
                    businessName = profileStore.getName().ifBlank { null },
                    licenseNumber = profileStore.getLicenseNumber().ifBlank { null },
                    businessAddress = profileStore.getBusinessAddress().ifBlank { null },
                    businessPhone = profileStore.getBusinessPhone().ifBlank { null },
                    logoUri = profileStore.getLogoPath(),
                    signatureUri = profileStore.getSignaturePath(),
                    currencyCode = settingsStore.getCurrencyCode(),
                    measurementUnit = settingsStore.getMeasurementUnit().name,
                    updatedAt = Clock.System.now().toEpochMilliseconds()
                )
            )
        }
    }

    /** Called once on login (RootNavHost) — pulls the remote row into local
     *  Settings/AppraiserProfileStore. Fields are only overwritten when the
     *  remote value is present, so a partially-filled local profile from an
     *  in-progress first-run isn't clobbered by an empty remote row. */
    suspend fun pullRemote(): Result<Unit> = runCatching {
        val userId = supabase.auth.currentUserOrNull()?.id ?: return@runCatching
        val remote = supabase.postgrest.from("business_profiles")
            .select { filter { eq("user_id", userId) } }
            .decodeSingleOrNull<RemoteBusinessProfile>() ?: return@runCatching

        remote.businessName?.let { profileStore.setName(it) }
        remote.licenseNumber?.let { profileStore.setLicenseNumber(it) }
        remote.businessAddress?.let { profileStore.setBusinessAddress(it) }
        remote.businessPhone?.let { profileStore.setBusinessPhone(it) }
        remote.logoUri?.let { profileStore.setLogoPath(it) }
        remote.signatureUri?.let { profileStore.setSignaturePath(it) }
        remote.currencyCode?.let { settingsStore.setCurrencyCode(it) }
        remote.measurementUnit?.let { raw ->
            runCatching { MeasurementUnit.valueOf(raw) }.getOrNull()?.let(settingsStore::setMeasurementUnit)
        }
    }
}