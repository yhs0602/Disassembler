package ui.treeitem

import org.jetbrains.skia.Drawable
import ui.TreeNode
import java.io.File

sealed class FileDrawerTreeItem(
    var caption: String,
    var level: Int,
    var tag: Any? = null,  // number or path or object
    var drawable: Drawable? = null
) : TreeNode<FileDrawerTreeItem> {
    abstract val isOpenable: Boolean

    companion object {
        fun fromFile(childLevel: Int, child: File?): FileDrawerTreeItem {
            return if (child == null) {
                EmptyItem(childLevel)
            } else if (child.isDirectory) {
                Folder(childLevel, child)
            } else {
                // TODO
                EmptyItem(childLevel)
            }
        }
    }
}