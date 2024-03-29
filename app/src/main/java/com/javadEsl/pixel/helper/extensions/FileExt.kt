package com.javadEsl.pixel.helper.extensions

import java.io.File

internal val File.size get() = if (!exists()) 0.0 else length().toDouble()
internal val File.sizeInKb get() = size / 1024
internal val File.sizeInMb get() = sizeInKb / 1024
internal val File.sizeInGb get() = sizeInMb / 1024
internal val File.sizeInTb get() = sizeInGb / 1024

/**
 * Format file size
 *
 * @return string ex: 2.35 MB
 */
internal fun File.size(): String = when {
    sizeInGb > 1024 -> "${sizeInTb.roundTo()} TB"
    sizeInMb > 1024 -> "${sizeInGb.roundTo()} GB"
    sizeInKb > 1024 -> "${sizeInMb.roundTo()} MB"
    size > 1024     -> "${sizeInKb.roundTo()} KB"
    else            -> "${size.roundTo()} Bytes"
}

/**
 * Path name
 *
 * @return
 */
internal fun File.lastPathTitle(): CharSequence {
    val paths = path.split("/")
    val titleIndex = paths.lastIndex - 1
    if (titleIndex >= 0) return paths[titleIndex]
    return ""
}
