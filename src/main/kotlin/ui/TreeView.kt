package ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface TreeNode<T : TreeNode<T>> {
    fun isExpandable(): Boolean
    fun getChildren(): List<T>
}


@Composable
fun <T : TreeNode<T>> ColumnScope.TreeView(
    rootNodeModel: T,
    expansionMap: SnapshotStateMap<T, Boolean>,
    NodeBox: @Composable (Modifier, nodeModel: T, expanded: Boolean, handleExpand: () -> Unit) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val isLoading = remember { mutableStateOf(false) }
    val items = remember { mutableStateListOf<TreeItem<T>>() }
    val recomposer = remember { mutableStateOf(true) }
    LaunchedEffect(rootNodeModel, expansionMap.entries, recomposer.value) {
        isLoading.value = true
        coroutineScope.launch {
            val flattenedItems = getFlattenedItemsAsync(rootNodeModel, expansionMap)
            items.clear()
            items.addAll(flattenedItems)
            isLoading.value = false
        }
    }
    if (isLoading.value) {
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
    }
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(items, key = { it.node.hashCode() }) { item ->
            NodeBox(Modifier.padding(start = (20 * item.level).dp), item.node, item.isExpanded) {
                val currentlyExpanded = expansionMap[item.node] ?: false
                expansionMap[item.node] = !currentlyExpanded
                recomposer.value = !recomposer.value
            }
        }
    }
}

data class TreeItem<T>(val node: T, val level: Int, val isExpanded: Boolean)

suspend fun <T : TreeNode<T>> getFlattenedItemsAsync(
    node: T,
    expansionMap: MutableMap<T, Boolean>,
    level: Int = 0
): List<TreeItem<T>> = withContext(Dispatchers.IO) {
    val result = mutableListOf<TreeItem<T>>()
    val expanded = expansionMap.getOrDefault(node, false)
    result.add(TreeItem(node, level, expanded))
    if (node.isExpandable() && expanded) {
        node.getChildren().forEach { child ->
            result.addAll(getFlattenedItemsAsync(child, expansionMap, level + 1))
        }
    }
    result
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