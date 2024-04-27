package ui.treeitem

class PEILField(level: Int, caption: String) : FileDrawerTreeItem(caption, level) {
    override val isOpenable: Boolean = false

    override fun isExpandable() = false

    override fun getChildren(): List<FileDrawerTreeItem> {
        return emptyList()
    }
}