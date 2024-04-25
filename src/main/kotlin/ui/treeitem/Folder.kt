package ui.treeitem

import java.io.File


class Folder(
    level: Int,
    private val file: File,
) : FileDrawerTreeItem(
    caption = file.name,
    level = level,
) {
    init {
        assert(file.isDirectory) { "File must be a directory" }
    }

    override val isOpenable = false

    override fun isExpandable() = true

    override fun getChildren(): List<FileDrawerTreeItem> {
        val items: MutableList<FileDrawerTreeItem> = mutableListOf()
        val childLevel = level + 1
        if (file.canRead()) {
            file.listFiles()?.let {
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
            items.add(PermissionDeniedItem(childLevel, file))
        }
        return items
    }
}