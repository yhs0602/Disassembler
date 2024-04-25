package viewmodel

import androidx.compose.runtime.mutableStateOf
import ui.treeitem.FileDrawerTreeItem
import java.io.File

class MainViewModel {
    val fileDrawerRootNode = mutableStateOf(FileDrawerTreeItem.fromFile(0, File("/")))

    fun onOpenDrawerItem(item: FileDrawerTreeItem) {

    }
}