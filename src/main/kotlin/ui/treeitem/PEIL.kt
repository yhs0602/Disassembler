package ui.treeitem

import at.pollaknet.api.facile.Facile
import ui.tab.tabkind.TabKind
import java.io.File

class PEIL(level: Int, private val file: File) : FileDrawerTreeItem(file.name, level), Openable {

    override fun isExpandable(): Boolean = true

    override fun getChildren(): List<FileDrawerTreeItem> {
        val childLevel = level + 1
        val items = mutableListOf<FileDrawerTreeItem>()
        try {
            val facileReflector = Facile.load(file.absolutePath)
            val assembly = facileReflector.loadAssembly()
            val types = assembly.allTypes
            for (type in types) {
                items.add(
                    PEILType(
                        childLevel,
                        type.namespace + "." + type.name,
                        file,
                        facileReflector,
                        type
                    )
                )
            }
        } catch (e: Exception) {
            items.add(ErrorItem(childLevel, file, e))
        }
        return items
    }

    override fun toTabKind(): TabKind {
        TODO("Not yet implemented")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PEIL

        return file == other.file
    }

    override fun hashCode(): Int {
        return file.hashCode()
    }
}