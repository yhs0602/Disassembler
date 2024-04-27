package ui.tab

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import ui.tab.tabkind.TabKind

// TODO: FileDrawerTreeItem type can be different from OpenAs type
// And they will be declared in a json settings file
@Composable
fun TabContent(content: TabKind?, logContent: String) {
    when (content) {
        null -> Text("No content")
        is TabKind.AnalysisResult -> AnalysisResultTabContent(content)
        is TabKind.Binary -> BinaryTabContent(content)
        is TabKind.FoundString -> FoundStringTabContent(content)
        is TabKind.Hex -> HexTabContent(content)
        is TabKind.Image -> ImageTabContent(content)
        TabKind.Log -> LogTabContent()
        is TabKind.Text -> TextTabContent(content)
    }
}