package com.example.luxeval.data.repository

import com.example.luxeval.presentation.photo.PhotoStorage
import com.example.luxeval.presentation.photo.cacheKeyFor
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.storage.storage
import kotlin.time.Clock
import kotlin.time.toDuration
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private const val SIGNED_URL_TTL_SECONDS = 3600  // 1 hour — refreshed on demand, so short is safer than long

/** In-memory cache so repeated views of the same file don't re-mint a
 *  signed URL on every recomposition. Keyed by the full "supabase://bucket/path"
 *  URI so entries from different buckets never collide. */
private class SignedUrlCache {
    private data class Entry(val url: String, val expiresAtEpochSeconds: Long)
    private val cache = mutableMapOf<String, Entry>()

    fun get(uri: String): String? {
        val entry = cache[uri] ?: return null
        val now = Clock.System.now().epochSeconds
        return if (now < entry.expiresAtEpochSeconds - 60) entry.url else null
    }

    fun put(uri: String, url: String) {
        cache[uri] = Entry(url, Clock.System.now().epochSeconds + SIGNED_URL_TTL_SECONDS)
    }
}

/**
 * Generalized remote-file store. URIs are shaped "supabase://<bucket>/<path>"
 * so the bucket travels with the reference itself — callers (item photos,
 * business logo/signature, anything added later) never need to know or pass
 * the bucket again once the URI exists.
 *
 * BREAKING FORMAT CHANGE from the previous single-bucket version: old URIs
 * were "supabase://<path>" with the bucket implied as "item-photos". Since
 * photo upload was still failing at the time this changed (missing
 * install(Storage) plugin — see project notes), there should be no real
 * uploaded data using the old format to migrate.
 */
class PhotoRepository(
    private val supabase: SupabaseClient,
    private val photoStorage: PhotoStorage
) {
    private val signedUrlCache = SignedUrlCache()

    /** Uploads a local file to the given bucket/path and returns its durable
     *  "supabase://bucket/path" reference (NOT a URL — URLs are minted fresh
     *  via [resolveUrl]). `upsert = true` lets callers reuse a stable path
     *  (e.g. "$userId/logo.jpg") and simply overwrite on re-upload. */
    suspend fun uploadFile(bucket: String, storagePath: String, localPath: String): String {
        println("BUCKET Upload$bucket $storagePath $localPath")
        val bytes = photoStorage.readImage(localPath)
        supabase.storage.from(bucket).upload(storagePath, bytes) { upsert = true }
        return "supabase://$bucket/$storagePath"
    }
    suspend fun resolveLocalPath(uri: String): String {
        if (!uri.startsWith("supabase://")) return uri // local file — nothing to resolve

        val key = cacheKeyFor(uri)
        if (photoStorage.cachedFileExists(key)) {
            return photoStorage.cachedFilePath(key)
        }

        val (bucket, path) = parseUri(uri)
        val bytes = supabase.storage.from(bucket).downloadAuthenticated(path)
        return photoStorage.saveCachedFile(key, bytes)
    }

    /** Uploads a locally-saved item photo. Kept as its own method (rather than
     *  making every call site build the path) since the itemId-scoped path
     *  convention is specific to item photos. */
    @OptIn(ExperimentalUuidApi::class)
    suspend fun uploadPhoto(itemId: String, localPath: String): String {
        val userId = requireNotNull(supabase.auth.currentUserOrNull()?.id) {
            "Cannot upload photo without an authenticated user"
        }
        val extension = localPath.substringAfterLast('.', "jpg")
        val storagePath = "$userId/$itemId/${Uuid.random()}.$extension"
        return uploadFile(ITEM_PHOTOS_BUCKET, storagePath, localPath)
    }

    /** Resolves any photoUri (local path OR supabase:// key) into something
     *  Coil/WebView can load directly. Local paths pass through unchanged. */
    suspend fun resolveUrl(uri: String): String {
        if (!uri.startsWith("supabase://")) return uri  // local path — nothing to resolve

        signedUrlCache.get(uri)?.let { return it }
        val (bucket, path) = parseUri(uri)
        println("RESOLVE URI   = $uri")
        println("RESOLVE BUCKET = $bucket")
        println("RESOLVE PATH   = $path")

        val signedUrl = supabase.storage.from(bucket)
            .createSignedUrl(path, expiresIn = SIGNED_URL_TTL_SECONDS.toDuration(kotlin.time.DurationUnit.SECONDS))
        signedUrlCache.put(uri, signedUrl)
        return signedUrl
    }

    suspend fun deletePhoto(uri: String) {
        if (!uri.startsWith("supabase://")) return
        val (bucket, path) = parseUri(uri)
        runCatching { supabase.storage.from(bucket).delete(path) }
    }

    private fun parseUri(uri: String): Pair<String, String> {
        val rest = uri.removePrefix("supabase://")

        return when {
            rest.startsWith("$ITEM_PHOTOS_BUCKET/") -> {
                ITEM_PHOTOS_BUCKET to rest.removePrefix("$ITEM_PHOTOS_BUCKET/")
            }

            rest.startsWith("$BUSINESS_ASSETS_BUCKET/") -> {
                BUSINESS_ASSETS_BUCKET to rest.removePrefix("$BUSINESS_ASSETS_BUCKET/")
            }

            else -> {
                // Legacy format:
                // supabase://<userId>/<itemId>/<filename>
                ITEM_PHOTOS_BUCKET to rest
            }
        }
    }
//    private fun parseUri(uri: String): Pair<String, String> {
//        val rest = uri.removePrefix("supabase://")
//        val bucket = rest.substringBefore('/')
//        val path = rest.substringAfter('/')
//        return bucket to path
//    }

    companion object {
        const val ITEM_PHOTOS_BUCKET = "item-photos"
        const val BUSINESS_ASSETS_BUCKET = "business-assets"
    }
}