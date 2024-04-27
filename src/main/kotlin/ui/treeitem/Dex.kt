package ui.treeitem

import jadx.api.JadxArgs
import jadx.api.JadxDecompiler
import storage.ProjectManager
import java.io.File


class Dex(level: Int, private val file: File) : FileDrawerTreeItem(file.name, level, drawable = "dex.png") {
    private val expansionDirectory: File by lazy {
        val targetDirectory = ProjectManager.getDirectoryForExpansion(file)
        if (!targetDirectory.exists()) {
            extractDex(targetDirectory)
        }
        targetDirectory
    }


    override fun isExpandable(): Boolean = true

    override fun getChildren(): List<FileDrawerTreeItem> {
        return expandFolderImpl(level + 1, expansionDirectory)
    }

    private fun extractDex(targetDir: File) {
        println("Getting children for dex file: ${file.absolutePath}")
        if (!targetDir.exists() && !targetDir.mkdirs()) {
            println("Failed to create directory: ${targetDir.absolutePath}")
            return
        }
        // TODO: Use jadx to decompile the dex file? Or just list the classes?
        val jadxArgs = JadxArgs()
        jadxArgs.setInputFile(file)
        jadxArgs.outDir = targetDir
        println("Decompiling dex file: ${file.absolutePath} to ${jadxArgs.outDir.absolutePath}")
        try {
            JadxDecompiler(jadxArgs).use { jadx ->
                jadx.load()
                jadx.save()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Dex

        return file == other.file
    }

    override fun hashCode(): Int {
        return file.hashCode()
    }
}