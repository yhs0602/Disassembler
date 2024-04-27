package ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ui.tab.TabContent
import viewmodel.MainViewModel

@Composable
fun WorkspaceView(viewModel: MainViewModel) {
    var tabIndex by remember { mutableStateOf(0) }
    val openTabCount = viewModel.openTabs.size
    if (openTabCount == 0) {
        Text("No open tabs")
    } else {
        Column(Modifier.fillMaxWidth()) {
            TabRow(selectedTabIndex = tabIndex) {
                viewModel.openTabs.forEachIndexed { index, item ->
                    Tab(text = { Text(item.caption) },
                        selected = tabIndex == index,
                        onClick = { tabIndex = index }
                    )
                }
            }
            TabContent(viewModel.openTabs.getOrNull(tabIndex))
        }
    }
}
