package ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

interface TreeNode<T : TreeNode<T>> {
    fun isExpandable(): Boolean
    fun getChildren(): List<T>
}


@Composable
fun <T : TreeNode<T>> TreeView(
    rootNodeModel: T,
    expansionMap: SnapshotStateMap<T, Boolean>,
    NodeBox: @Composable (Modifier, nodeModel: T, expanded: Boolean, handleExpand: () -> Unit) -> Unit
) {
    LazyColumn(modifier=Modifier.fillMaxWidth()) {
        items(getFlattenedItems(rootNodeModel, expansionMap), key = { it.node.hashCode() }) { item ->
            NodeBox(Modifier.padding(start = (20 * item.level).dp), item.node, item.isExpanded) {
                val currentlyExpanded = expansionMap[item.node] ?: false
                expansionMap[item.node] = !currentlyExpanded
            }
        }
    }
}

data class TreeItem<T>(val node: T, val level: Int, val isExpanded: Boolean)


fun <T : TreeNode<T>> getFlattenedItems(
    node: T,
    expansionMap: MutableMap<T, Boolean>,
    level: Int = 0
): List<TreeItem<T>> {
    val result = mutableListOf<TreeItem<T>>()
    val expanded = expansionMap.getOrDefault(node, false)
    result.add(TreeItem(node, level, expanded))
    if (node.isExpandable() && expanded) {
        node.getChildren().forEach { child ->
            result.addAll(getFlattenedItems(child, expansionMap, level + 1))
        }
    }
    return result
}

class TestTreeNode : TreeNode<TestTreeNode> {
    override fun isExpandable(): Boolean {
        return true
    }

    override fun getChildren(): List<TestTreeNode> {
        return listOf(TestTreeNode(), TestTreeNode())
    }
}

@Preview
@Composable
fun TestTreeView() {
//    TreeView(nodeModel = TestTreeNode()) { nodeModel, expanded, onClick ->
//        Text("Item", modifier = Modifier.clickable(onClick = onClick))
//    }
}