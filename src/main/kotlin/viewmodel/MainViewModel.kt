package viewmodel

import androidx.compose.runtime.mutableStateOf
import ui.treeitem.EmptyItem
import ui.treeitem.FileDrawerTreeItem
import java.io.File

class MainViewModel {
    val fileDrawerRootNode = mutableStateOf<FileDrawerTreeItem>(EmptyItem(0))

    fun onOpenDrawerItem(item: FileDrawerTreeItem) {

    }

    fun loadFiles(selectedZipFilePath: String) {
        print(selectedZipFilePath)
        fileDrawerRootNode.value = FileDrawerTreeItem.fromFile(0, File(selectedZipFilePath))
    }
}