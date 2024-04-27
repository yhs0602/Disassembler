package ui.treeitem

import jadx.api.JadxArgs
import jadx.api.JadxDecompiler
import java.io.File


class Dex(level: Int, private val file: File) : FileDrawerTreeItem(file.name, level) {
    override val isOpenable: Boolean = false

    override fun isExpandable(): Boolean = true

    override fun getChildren(): List<FileDrawerTreeItem> {
        val childLevel = level + 1
        val items = mutableListOf<FileDrawerTreeItem>()
        // TODO: Use jadx to decompile the dex file? Or just list the classes?
        // TODO: Use correct path
        val jadxArgs = JadxArgs()
        jadxArgs.setInputFile(file)
        jadxArgs.outDir = File("output")
        try {
            JadxDecompiler(jadxArgs).use { jadx ->
                jadx.load()
                jadx.save()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return items
    }
}