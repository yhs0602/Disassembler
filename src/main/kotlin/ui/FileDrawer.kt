package ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ui.treeitem.FileDrawerTreeItem
import viewmodel.MainViewModel

@ExperimentalFoundationApi
@Composable
fun FileDrawer(viewModel: MainViewModel) {
//    val askOpen = viewModel.askOpen.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .background(Color.White)
    ) {
        Column {
            IconButton(onClick = { }) {
                Icons.Outlined.Refresh
            }
            val rootFileNode = viewModel.fileDrawerRootNode.value
            TreeView(nodeModel = rootFileNode) { node, expanded, handleExpand ->
                FileDrawerItemRow(node, expanded, handleExpand, viewModel::onOpenDrawerItem)
            }
        }
    }

//    askOpenDialog(askOpen, viewModel)
}

@ExperimentalFoundationApi
@Composable
private fun FileDrawerItemRow(
    node: FileDrawerTreeItem,
    expanded: Boolean,
    handleExpand: () -> Unit,
    onOpenDrawerItem: (FileDrawerTreeItem) -> Unit
) {
    Row(
        modifier = Modifier.combinedClickable(
            onClick = {
                if (node.isExpandable()) {
                    handleExpand()
                } else if (node.isOpenable) {
                    onOpenDrawerItem(node)
                }
            },
            onLongClick = {
                if (node.isOpenable) {
                    onOpenDrawerItem(node)
                }
            },
        ), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        val expandable = node.isExpandable()
        Icon(
            painter = painterResource(
                if (expandable) {
                    if (expanded) {
                        "expand_less.png"
                    } else {
                        "expand_more.png"
                    }
                } else {
                    "empty.png"
                }
            ),
            contentDescription = "expand",
            Modifier.width(20.dp),
            tint = Color.Gray
        )
        Icon(
            painter = painterResource(
                "folder.png"
            ),
            contentDescription = "Folder",
            Modifier.width(20.dp),
            tint = if (expandable) Color(0xFF7F00FF) else LocalContentColor.current
        )
        Text(text = node.caption)
    }
}
