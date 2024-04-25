package ui.treeitem

import java.io.File

class PermissionDeniedItem(level: Int, file: File) : FileDrawerTreeItem("Permission Denied: ${file.name}", level) {
    override fun isExpandable() = false

    override fun getChildren(): List<FileDrawerTreeItem> = emptyList()
}