package com.greenslocks.iptv

import java.net.URI

/** The title and APK version code must share the same increasing build number. */
fun previewBuildNumber(title: String): Int? = Regex("^Vance TV Preview build (\\d+)$")
    .matchEntire(title)?.groupValues?.get(1)?.toIntOrNull()?.takeIf { it > 10_000 }

fun validPreviewDownload(url: String): Boolean = runCatching {
    val uri = URI(url)
    uri.scheme == "https" && uri.host == "github.com" && uri.userInfo == null && uri.port == -1 &&
        uri.query == null && uri.fragment == null &&
        uri.path == "/jamarius997-a11y/Greens-Locks/releases/download/preview-latest/VanceTV-Preview.apk"
}.getOrDefault(false)
