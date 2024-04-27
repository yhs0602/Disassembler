package storage

import org.apache.commons.codec.digest.MurmurHash3
import java.io.File
import java.io.FileInputStream
import java.io.IOException


// Handles the project management
object ProjectManager {
    private lateinit var rootDirectory: File

    fun initialize(directory: File) {
        rootDirectory = directory
    }

    fun getPrivateDirectory(): File {
        return File(rootDirectory, ".reverser")
    }

    fun getCacheDirectory(): File {
        return File(getPrivateDirectory(), "cache")
    }

    @Throws(IOException::class)
    fun getDirectoryForExpansion(fromFile: File): File {
        // Get murmur hash from file
        val hexHashValue: String
        try {
            FileInputStream(fromFile).use { inputStream ->
                val hasher = MurmurHash3.IncrementalHash32x86()
                val buffer = ByteArray(1024) // 버퍼 크기 설정
                var readBytes: Int

                while ((inputStream.read(buffer).also { readBytes = it }) != -1) {
                    // ByteBuffer를 사용하여 바이트 배열을 해시에 피드
                    hasher.add(buffer, 0, readBytes)
                }

                val hashValue: Int = hasher.end()
                hexHashValue = Integer.toHexString(hashValue) // lowercase
            }
        } catch (e: IOException) {
            throw e
        }
        // Use murmur hash as directory name
        return File(getCacheDirectory(), hexHashValue)
    }

    fun invalidateCache() {
        if (!this::rootDirectory.isInitialized) {
            println("Root directory is not initialized")
            return
        }
        val cacheDirectory = getCacheDirectory()
        if (cacheDirectory.exists()) {
            cacheDirectory.deleteRecursively()
        }
    }
}