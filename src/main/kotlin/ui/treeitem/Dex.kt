package ui.treeitem

import java.io.File

class Dex(level: Int, file: File) : FileDrawerTreeItem(file.name, level) {
    override val isOpenable: Boolean = false

    override fun isExpandable(): Boolean = true

    override fun getChildren(): List<FileDrawerTreeItem> {
        val targetDirectory =
            ProjectDataStorage.resolveToWrite(ProjectManager.getRelPath(filename), true)
//                val targetDirectory = File(File(appCtx.filesDir, "/dex-decompiled/"), File(filename).name + "/")
        targetDirectory.mkdirs()
        Main.main(arrayOf("d", "-o", targetDirectory.absolutePath, filename))
        // finishHandler()
        return FileDrawerTreeItem(targetDirectory, initialLevel).getChildren()

    }
}