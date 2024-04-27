package ui.treeitem

import ui.treeitem.FileDrawerTreeItem.Companion.fromFile
import java.io.File


class Folder(
    level: Int,
    private val file: File,
) : FileDrawerTreeItem(
    caption = file.name.removeSuffix("/") + "/",
    level = level,
    drawable = "folder.png"
) {
    init {
        assert(file.isDirectory) { "File must be a directory" }
    }


    override fun isExpandable() = true

    override fun getChildren(): List<FileDrawerTreeItem> {
        val childLevel = level + 1
        return expandFolderImpl(childLevel, file)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Folder

        return file == other.file
    }

    override fun hashCode(): Int {
        return file.hashCode()
    }
}


fun expandFolderImpl(childLevel: Int, folder: File): List<FileDrawerTreeItem> {
    val items: MutableList<FileDrawerTreeItem> = mutableListOf()
    if (folder.canRead()) {
        folder.listFiles()?.let {
            if (it.isEmpty()) {
                items.add(EmptyItem(childLevel))
                return@let
            }
            it.forEach { child ->
                if (child.isDirectory) {
                    items.add(Folder(childLevel, child))
                } else {
                    items.add(fromFile(childLevel, child))
                }
            }
            items.sortWith(FileNameComparator)
        }
    } else {
        items.add(PermissionDeniedItem(childLevel, folder))
    }
    return items
}