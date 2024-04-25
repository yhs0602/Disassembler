package ui.treeitem

class ErrorItem(level: Int, throwable: Throwable) : FileDrawerTreeItem("Error", level) {
    override val isOpenable: Boolean = true

    override fun isExpandable() = false

    override fun getChildren(): List<FileDrawerTreeItem> = emptyList()
}