package ui.treeitem

import ui.tab.tabkind.TabKind
import java.io.File

class PE(level: Int, val file: File) : FileDrawerTreeItem(file.name, level), Openable {

    override fun isExpandable(): Boolean = false

    override fun getChildren(): List<FileDrawerTreeItem> = TODO("Not supported")
    override fun toTabKind(): TabKind = TabKind.Binary(file)
}