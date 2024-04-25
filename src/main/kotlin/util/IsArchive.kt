package util

import org.apache.commons.compress.archivers.ArchiveStreamFactory
import java.io.BufferedInputStream
import java.io.File

fun File.isArchive(): Boolean {
    return try {
        ArchiveStreamFactory.detect(BufferedInputStream(inputStream()))
        true
    } catch (e: Exception) {
        false
    }
}
