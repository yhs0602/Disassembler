package ui.treeitem

class EmptyItem(level: Int) : FileDrawerTreeItem("Empty", level) {

    override fun isExpandable() = false

    override fun getChildren(): List<FileDrawerTreeItem> = TODO("Not supported")
}