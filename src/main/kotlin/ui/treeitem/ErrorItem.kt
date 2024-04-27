package ui.treeitem

import storage.ProjectManager
import ui.tab.tabkind.TabKind
import java.io.File

class ErrorItem(level: Int, val file: File, private val throwable: Throwable) : FileDrawerTreeItem("Error", level),
    Openable {
    override fun isExpandable() = false

    override fun getChildren(): List<FileDrawerTreeItem> = emptyList()
    override fun toTabKind(): TabKind {
        val key = "error_${file.name}"
        val fileToWrite = ProjectManager.getFileFromKey(file, key)
        val renderedString = throwable.stackTraceToString()
        fileToWrite.writeText(renderedString)
        return TabKind.Text(fileToWrite)
    }
}