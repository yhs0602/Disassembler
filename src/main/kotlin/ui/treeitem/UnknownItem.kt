package ui.treeitem

import java.io.File

class Unknown(level: Int, file: File) : FileDrawerTreeItem(file.name, level, drawable = "unknown.png") {
    override val isOpenable: Boolean = false

    override fun isExpandable(): Boolean = false

    override fun getChildren(): List<FileDrawerTreeItem> {
        TODO("Not yet implemented")
    }
}