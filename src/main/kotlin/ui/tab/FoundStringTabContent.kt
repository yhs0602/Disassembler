package ui.tab

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import ui.tab.tabkind.TabKind

@Composable
fun FoundStringTabContent(content: TabKind.FoundString) {
    Text("Text tab content")
}