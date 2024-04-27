package viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import storage.ProjectManager
import ui.tab.tabkind.TabKind
import ui.treeitem.EmptyItem
import ui.treeitem.FileDrawerTreeItem
import ui.treeitem.Openable
import java.io.File

class MainViewModel {
    val logContent: String
        get() = "Log content"
    val fileDrawerRootNode = mutableStateOf<FileDrawerTreeItem>(EmptyItem(0))
    val expansionMap = mutableStateMapOf<FileDrawerTreeItem, Boolean>()
    val openTabs = mutableStateListOf<TabKind>()
    val tabIndex = mutableStateOf(0)

    // Open the drawer item as a tab in the editor
    fun onOpenDrawerItem(item: Openable) {
        println("Opening item: $item")
        val tabKind = item.toTabKind() // TODO: Use item as Key for efficient lookup
        val existingIndex = openTabs.indexOf(tabKind)
        if (existingIndex == -1) {
            openTabs.add(tabKind)
        } else {
            tabIndex.value = existingIndex
        }
    }

    fun loadFiles(selectedZipFilePath: String) {
        print(selectedZipFilePath)
        ProjectManager.initialize(File(selectedZipFilePath))
        fileDrawerRootNode.value = FileDrawerTreeItem.fromFile(0, File(selectedZipFilePath))
    }

    fun onTabIndexChanged(i: Int) {
        tabIndex.value = i
    }
}