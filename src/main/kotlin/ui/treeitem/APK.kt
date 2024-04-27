package ui.treeitem


import storage.ProjectManager
import java.io.File

class APK(level: Int, val file: File) : FileDrawerTreeItem(file.name, level) {
    private val expansionDirectory: File by lazy {
        val targetDirectory = ProjectManager.getDirectoryForExpansion(file)
        if (!targetDirectory.exists()) {
            ArchiveExtractor.extract(file, targetDirectory)
        }
        targetDirectory
    }

    override val isOpenable: Boolean = false

    override fun isExpandable(): Boolean = true

    override fun getChildren(): List<FileDrawerTreeItem> {
        return expandFolderImpl(level + 1, expansionDirectory)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as APK

        if (file != other.file) return false
        if (expansionDirectory != other.expansionDirectory) return false

        return true
    }

    override fun hashCode(): Int {
        var result = file.hashCode()
        result = 31 * result + expansionDirectory.hashCode()
        return result
    }
}