package ui.treeitem

import java.io.File

class Elf (level: Int, file: File) : FileDrawerTreeItem(file.name, level) {
    override val isOpenable: Boolean
        get() = TODO("Not yet implemented")

    override fun isExpandable(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getChildren(): List<FileDrawerTreeItem> {
        TODO("Not yet implemented")
    }
}