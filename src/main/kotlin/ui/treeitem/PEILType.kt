package ui.treeitem

import at.pollaknet.api.facile.FacileReflector
import at.pollaknet.api.facile.symtab.symbols.Type

class PEILType(
    level: Int,
    caption: String,
    facileReflector: FacileReflector,
    type: Type
) : FileDrawerTreeItem(caption, level) {
    override val isOpenable: Boolean
        get() = TODO("Not yet implemented")

    override fun isExpandable(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getChildren(): List<FileDrawerTreeItem> {
        return emptyList()
    }
}