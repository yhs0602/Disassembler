package ui.treeitem

class EmptyItem(level: Int) : FileDrawerTreeItem("Empty", level) {
    override val isOpenable: Boolean = false

    override fun isExpandable() = false

    override fun getChildren(): List<FileDrawerTreeItem> = emptyList()
}