package ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ui.tab.TabContent
import viewmodel.MainViewModel

@Composable
fun WorkspaceView(viewModel: MainViewModel, tabIndex: Int, onTabIndexChanged: (Int) -> Unit) {
    val openTabCount = viewModel.openTabs.size
    if (openTabCount == 0) {
        Text("No open tabs")
    } else {
        Column(Modifier.fillMaxWidth()) {
            TabRow(selectedTabIndex = tabIndex) {
                viewModel.openTabs.forEachIndexed { index, item ->
                    Tab(text = { Text(item.caption) },
                        selected = tabIndex == index,
                        onClick = { onTabIndexChanged(index) }
                    )
                }
            }
            TabContent(viewModel.openTabs.getOrNull(tabIndex), viewModel.logContent)
        }
    }
}
