package com.example.luxeval.presentation.util

// presentation/util/ImageLoaderInit.kt (call once from App() or MainActivity/iOS entry)
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.SingletonImageLoader

fun initImageLoader(context: PlatformContext) {
    SingletonImageLoader.setSafe { context ->
        ImageLoader.Builder(context)
            .components { add(KtorNetworkFetcherFactory()) }
            .build()
    }
}