package ui.treeitem

import ui.tab.tabkind.TabKind

interface Openable {
    fun toTabKind(): TabKind
}