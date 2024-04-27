package viewmodel

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import storage.ProjectManager
import ui.treeitem.EmptyItem
import ui.treeitem.FileDrawerTreeItem
import java.io.File

class MainViewModel {
    val fileDrawerRootNode = mutableStateOf<FileDrawerTreeItem>(EmptyItem(0))
    val expansionMap = mutableStateMapOf<FileDrawerTreeItem, Boolean>()

    fun onOpenDrawerItem(item: FileDrawerTreeItem) {
        println("Opening item: ${item.caption}")
    }

    fun loadFiles(selectedZipFilePath: String) {
        print(selectedZipFilePath)
        ProjectManager.initialize(File(selectedZipFilePath))
        fileDrawerRootNode.value = FileDrawerTreeItem.fromFile(0, File(selectedZipFilePath))
    }
}