package ui.treeitem

import at.pollaknet.api.facile.Facile
import java.io.File

class PEIL(level: Int, private val file: File) : FileDrawerTreeItem(file.name, level) {
    override val isOpenable: Boolean = true

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
                        facileReflector,
                        type
                    )
                )
            }
        } catch (e: Exception) {
            items.add(ErrorItem(childLevel, e))
        }
        return items
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