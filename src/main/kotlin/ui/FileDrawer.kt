package ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ui.treeitem.FileDrawerTreeItem
import ui.treeitem.Openable
import viewmodel.MainViewModel

@ExperimentalFoundationApi
@Composable
fun FileDrawer(viewModel: MainViewModel) {
//    val askOpen = viewModel.askOpen.collectAsState()
    Box(
        modifier = Modifier
            .fillMaxWidth(0.3f)
            .fillMaxHeight()
            .background(Color.White)
            .padding(5.dp)
    ) {
        val stateHorizontal = rememberScrollState(0)
        val maxWidth = remember { mutableStateOf(0.dp) }
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            maxWidth.value = this.maxWidth
            Column(
                modifier = Modifier.fillMaxWidth().horizontalScroll(
                    stateHorizontal
                )
            ) {
                val rootFileNode = viewModel.fileDrawerRootNode.value
                TreeView(
                    rootNodeModel = rootFileNode,
                    expansionMap = viewModel.expansionMap
                ) { modifier, node, expanded, handleExpand ->
                    FileDrawerItemRow(
                        modifier.defaultMinSize(maxWidth.value),
                        node,
                        expanded,
                        handleExpand,
                        viewModel::onOpenDrawerItem
                    )
                }
            }
            HorizontalScrollbar(
                modifier = Modifier.align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(end = 10.dp),
                adapter = rememberScrollbarAdapter(stateHorizontal)
            )
        }
    }

//    askOpenDialog(askOpen, viewModel)
}

@ExperimentalFoundationApi
@Composable
private fun FileDrawerItemRow(
    modifier: Modifier,
    node: FileDrawerTreeItem,
    expanded: Boolean,
    handleExpand: () -> Unit,
    onOpenDrawerItem: (Openable) -> Unit
) {
    Row(
        modifier = modifier
            .combinedClickable(
                onClick = {
                    if (node.isExpandable()) {
                        handleExpand()
                    } else if (node is Openable) {
                        onOpenDrawerItem(node)
                    }
                },
                onLongClick = {
                    if (node is Openable) {
                        onOpenDrawerItem(node)
                    }
                },
            ),
        verticalAlignment = Alignment.CenterVertically,
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
                    "blank.png"
                }
            ),
            contentDescription = "expand",
            Modifier.width(20.dp),
            tint = Color.Gray
        )
        Icon(
            painter = painterResource(
                node.drawable ?: "empty.png"
            ),
            contentDescription = "Icon",
            Modifier.width(20.dp),
            tint = if (expandable) Color(0xFF7F00FF) else LocalContentColor.current
        )
        Text(text = node.caption)
    }
}
