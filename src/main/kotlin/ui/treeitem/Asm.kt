package ui.treeitem

import ui.tab.tabkind.TabKind
import java.io.File

class Asm(level: Int, val file: File) : FileDrawerTreeItem(file.name, level, drawable = "assembly.png"), Openable {

    override fun isExpandable() = false

    override fun getChildren(): List<FileDrawerTreeItem> = TODO("Not supported")

    override fun toTabKind(): TabKind {
        return TabKind.Text(file)
    }
}