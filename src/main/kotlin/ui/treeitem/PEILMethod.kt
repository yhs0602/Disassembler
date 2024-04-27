package ui.treeitem

import at.pollaknet.api.facile.FacileReflector
import at.pollaknet.api.facile.symtab.symbols.Method

class PEILMethod(
    level: Int,
    caption: String,
    private val reflector: FacileReflector,
    private val method: Method
) : FileDrawerTreeItem(caption, level) {
    override val isOpenable: Boolean = true

    override fun isExpandable(): Boolean = false

    override fun getChildren(): List<FileDrawerTreeItem> {
        return emptyList()
    }
}