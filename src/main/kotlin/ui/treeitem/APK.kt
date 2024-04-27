package ui.treeitem


import java.io.File

class APK(level: Int, file: File) : FileDrawerTreeItem(file.name, level) {
    override val isOpenable: Boolean = false

    override fun isExpandable(): Boolean = true

    override fun getChildren(): List<FileDrawerTreeItem> {
        TODO("Not yet implemented")
    }
}