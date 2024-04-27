package ui.tab

import androidx.compose.material.Text
import androidx.compose.runtime.*
import ui.tab.tabkind.TabKind

@Composable
fun TextTabContent(content: TabKind.Text) {
    var text by remember { mutableStateOf("Text tab content") }
    LaunchedEffect(content) {
        text = content.file.readText()
    }
    Text(text)
}