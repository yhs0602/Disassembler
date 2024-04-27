package ui.treeitem

import at.pollaknet.api.facile.FacileReflector
import at.pollaknet.api.facile.renderer.ILAsmRenderer
import at.pollaknet.api.facile.symtab.symbols.Method
import storage.ProjectManager
import ui.tab.tabkind.TabKind
import java.io.File

class PEILMethod(
    level: Int,
    caption: String,
    private val file: File,
    private val reflector: FacileReflector,
    private val method: Method
) : FileDrawerTreeItem(caption, level), Openable {

    override fun isExpandable(): Boolean = false

    override fun getChildren(): List<FileDrawerTreeItem> {
        return emptyList()
    }

    override fun toTabKind(): TabKind {
        val reflector = reflector
        val method = method
        val renderedString = ILAsmRenderer(reflector).render(method)
        val key = "${method.owner.name}.${method.name}_${method.methodSignature}"
        val fileToWrite = ProjectManager.getFileFromKey(file, key)
        fileToWrite.writeText(renderedString)
        return TabKind.Text(fileToWrite)
    }
}