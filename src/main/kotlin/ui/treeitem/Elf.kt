package ui.treeitem

import java.io.File

class Elf(level: Int, file: File) : FileDrawerTreeItem(file.name, level, drawable = "terminal.png") {
    override val isOpenable: Boolean = true

    override fun isExpandable(): Boolean = false

    override fun getChildren(): List<FileDrawerTreeItem> {
        TODO("Not yet implemented")
    }
}