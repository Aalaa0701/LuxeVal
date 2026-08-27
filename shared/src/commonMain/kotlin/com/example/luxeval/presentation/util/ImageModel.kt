package com.example.luxeval.presentation.util

/**
 * Our PhotoStorage (both platforms) returns bare absolute paths, e.g.
 * "/data/user/0/.../photos/xyz.jpg". A bare path has no URI scheme, so Coil's
 * scheme-based fetcher matching silently fails to load it. Prefixing with file://
 * gives it an explicit scheme Coil resolves on both Android and iOS.
 */
import okio.Path
import okio.Path.Companion.toPath

/** Our PhotoStorage returns bare absolute paths on both platforms. Coil3 (multiplatform)
 *  resolves local files via okio.Path, not raw strings or java.io.File (JVM-only). */
fun localPhotoModel(path: String): Path = path.toPath()