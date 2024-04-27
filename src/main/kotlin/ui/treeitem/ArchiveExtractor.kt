package ui.treeitem

import org.apache.commons.compress.archivers.ArchiveInputStream
import org.apache.commons.compress.archivers.ArchiveStreamFactory
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.file.Files


object ArchiveExtractor {
    fun extract(archiveFile: File, targetDirectory: File) {
        if (!targetDirectory.exists() && !targetDirectory.mkdirs()) {
            throw IOException("Failed to create target directory: $targetDirectory")
        }
        FileInputStream(archiveFile).use { fileInputStream ->
            BufferedInputStream(fileInputStream).use { bufferedInputStream ->
                (ArchiveStreamFactory().createArchiveInputStream(bufferedInputStream) as ArchiveInputStream<*>)
                    .use { archiveInputStream ->
                        var entry = archiveInputStream.nextEntry
                        while (entry != null) {
                            if (!archiveInputStream.canReadEntryData(entry)) {
                                println("Skipping unreadable entry: $entry")
                            } else {
                                if (entry.name == "")
                                    continue
                                val childFile = File(targetDirectory, entry.name)
                                val canonicalPath = childFile.canonicalPath
                                if (!canonicalPath.startsWith(targetDirectory.canonicalPath + File.separator)) {
                                    throw SecurityException("Entry is outside of the target directory: $entry")
                                }
                                if (entry.isDirectory) {
                                    if (!childFile.exists() && !childFile.mkdirs()) {
                                        throw IOException("Failed to create directory: $childFile")
                                    }
                                } else {
                                    val parentFile = childFile.parentFile
                                    if (!parentFile.exists() && !parentFile.mkdirs()) {
                                        throw IOException("Failed to create directory: $parentFile")
                                    }
                                    Files.newOutputStream(childFile.toPath()).use { outputStream ->
                                        archiveInputStream.transferTo(outputStream)
                                    }
                                }
                            }
                            try {
                                entry = archiveInputStream.nextEntry
                            } catch (e: IOException) {
                                println("Failed to read next entry: $e")
                                entry = null
                            }
                        }
                    }
            }
        }
    }
}