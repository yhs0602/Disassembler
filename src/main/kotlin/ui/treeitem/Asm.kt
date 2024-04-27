package ui.treeitem

import java.io.File

class Asm(level: Int, file: File) : FileDrawerTreeItem(file.name, level, drawable = "assembly.png") {
    override val isOpenable: Boolean = true

    override fun isExpandable() = false

    override fun getChildren(): List<FileDrawerTreeItem> {
        TODO("Not yet implemented")
    }
}