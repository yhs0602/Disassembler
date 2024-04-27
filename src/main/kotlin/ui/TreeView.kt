package ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateMap

interface TreeNode<T : TreeNode<T>> {
    fun isExpandable(): Boolean
    fun getChildren(): List<T>
}


@Composable
fun <T : TreeNode<T>> TreeView(
    rootNodeModel: T,
    expansionMap: SnapshotStateMap<T, Boolean>,
    NodeBox: @Composable (nodeModel: T, expanded: Boolean, handleExpand: () -> Unit) -> Unit
) {
    LazyColumn {
        items(getFlattenedItems(rootNodeModel, expansionMap), key = { it.node.hashCode() }) { item ->
            NodeBox(item.node, item.isExpanded) {
                val currentlyExpanded = expansionMap[item.node] ?: false
                expansionMap[item.node] = !currentlyExpanded
                println("${item.node} is now ${!currentlyExpanded}")
                println("Expansion map: ${expansionMap.entries.joinToString() { "${it.key} -> ${it.value}" }}")
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