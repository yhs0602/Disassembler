package ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

interface TreeNode<T : TreeNode<T>> {
    fun isExpandable(): Boolean
    fun getChildren(): List<T>
}

@Composable
fun <T : TreeNode<T>> TreeView(
    nodeModel: T,
    NodeBox: @Composable (nodeModel: T, expanded: Boolean, handleExpand: () -> Unit) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    Column {
        NodeBox(nodeModel, isExpanded) {
            if (isExpanded) {
                isExpanded = false
            } else {
                if (nodeModel.isExpandable()) {
                    isExpanded = true
                }
            }
        }
        if (isExpanded) {
            LazyColumn(modifier = Modifier.padding(start = 20.dp)) {
                val children = nodeModel.getChildren()
                items(children) { child ->
                    TreeView(nodeModel = child, NodeBox = NodeBox)
                }
            }
//            val children = nodeModel.getChildren()
//            Row {
//                Spacer(modifier = Modifier.size(20.dp))
//                Column {
//                    children.forEach { model ->
//                        TreeView(nodeModel = model, NodeBox = NodeBox)
//                    }
//                }
//            }
        }
    }
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
    TreeView(nodeModel = TestTreeNode()) { nodeModel, expanded, onClick ->
        Text("Item", modifier = Modifier.clickable(onClick = onClick))
    }
}