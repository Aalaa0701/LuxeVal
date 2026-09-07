// data/repository/PhotoRepository.kt
package com.example.luxeval.data.repository

import com.example.luxeval.presentation.photo.PhotoStorage
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.storage.storage
import kotlin.time.Clock
import kotlin.time.toDuration
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private const val BUCKET = "item-photos"
private const val SIGNED_URL_TTL_SECONDS = 3600  // 1 hour — refreshed on demand, so short is safer than long

/** In-memory cache so repeated views of the same item don't re-mint a
 *  signed URL on every recomposition. Not persisted — a fresh URL is
 *  cheap to fetch and safer than caching something with an expiry. */
private class SignedUrlCache {
    private data class Entry(val url: String, val expiresAtEpochSeconds: Long)
    private val cache = mutableMapOf<String, Entry>()

    fun get(path: String): String? {
        val entry = cache[path] ?: return null
        val now = Clock.System.now().epochSeconds
        // Refresh a bit before actual expiry so an in-flight PDF export
        // or slow image load never races an expiring URL.
        return if (now < entry.expiresAtEpochSeconds - 60) entry.url else null
    }

    fun put(path: String, url: String) {
        cache[path] = Entry(url, Clock.System.now().epochSeconds + SIGNED_URL_TTL_SECONDS)
    }
}

class PhotoRepository(
    private val supabase: SupabaseClient,
    private val photoStorage: PhotoStorage
) {
    private val signedUrlCache = SignedUrlCache()

    /** Uploads a locally-saved photo and returns its durable storage key
     *  (NOT a URL — URLs are minted fresh via [resolveUrl]). */
    suspend fun uploadPhoto(itemId: String, localPath: String): String {
        val userId = requireNotNull(supabase.auth.currentUserOrNull()?.id) {
            "Cannot upload photo without an authenticated user"
        }
        val bytes = photoStorage.readImage(localPath)
        val extension = localPath.substringAfterLast('.', "jpg")
        @OptIn(kotlin.uuid.ExperimentalUuidApi::class)
        val storagePath = "$userId/$itemId/${Uuid.random()}.$extension"

        supabase.storage.from(BUCKET).upload(storagePath, bytes)
        return "supabase://$storagePath"
    }

    /** Resolves a photoUri (local path OR supabase:// key) into something
     *  Coil/WebView can load directly. Local paths pass through unchanged. */
    suspend fun resolveUrl(photoUri: String): String {
        if (!photoUri.startsWith("supabase://")) return photoUri  // local path — nothing to resolve

        val storagePath = photoUri.removePrefix("supabase://")
        signedUrlCache.get(storagePath)?.let { return it }

        val signedUrl = supabase.storage.from(BUCKET)
            .createSignedUrl(storagePath, expiresIn = SIGNED_URL_TTL_SECONDS.toDuration(kotlin.time.DurationUnit.SECONDS))
        signedUrlCache.put(storagePath, signedUrl)
        return signedUrl
    }

    suspend fun deletePhoto(photoUri: String) {
        if (!photoUri.startsWith("supabase://")) return
        val storagePath = photoUri.removePrefix("supabase://")
        runCatching { supabase.storage.from(BUCKET).delete(storagePath) }
    }
}