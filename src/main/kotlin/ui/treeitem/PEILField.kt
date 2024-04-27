package ui.treeitem

class PEILField(level: Int, caption: String) : FileDrawerTreeItem(caption, level) {

    override fun isExpandable() = false

    override fun getChildren(): List<FileDrawerTreeItem> = TODO("Not supported")
}