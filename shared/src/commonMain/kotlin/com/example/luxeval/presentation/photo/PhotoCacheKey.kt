package com.example.luxeval.presentation.photo


/** Deterministic filesystem-safe key for a photo URI, so the same remote photo
 *  always maps to the same cache file regardless of when we look it up. */
fun cacheKeyFor(uri: String): String {
    var hash = 2166136261L
    for (b in uri.encodeToByteArray()) {
        hash = hash xor (b.toLong() and 0xff)
        hash *= 16777619L
    }
    return hash.toString(16)
}