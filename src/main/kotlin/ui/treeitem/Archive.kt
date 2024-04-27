package ui.treeitem

import storage.ProjectManager
import java.io.File

class Archive(level: Int, private val file: File) : FileDrawerTreeItem(file.name, level) {
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

        other as Archive

        return file == other.file
    }

    override fun hashCode(): Int {
        return file.hashCode()
    }
}