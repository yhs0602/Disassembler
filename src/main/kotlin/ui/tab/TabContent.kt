package ui.tab

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import ui.treeitem.*

// TODO: FileDrawerTreeItem type can be different from OpenAs type
// And they will be declared in a json settings file
@Composable
fun TabContent(content: FileDrawerTreeItem?) {
    when (content) {
        null -> Text("No content")
        is APK -> TODO("Cannot open APK files")
        is Archive -> TODO("Cannot open archive files")
        is Asm -> AsmTabContent(content)
        is Dex -> TODO("Cannot open dex files")
        is Elf -> ElfTabContent(content)
        is EmptyItem -> TODO("Cannot open empty items")
        is ErrorItem -> TODO("Cannot open error items")
        is Folder -> TODO("Cannot open folders")
        is PE -> PETabContent(content)
        is PEIL -> PEILTabContent(content)
        is PEILField -> TODO("Cannot open PEIL fields")
        is PEILMethod -> PEILMethodTabContent(content)
        is PEILType -> TODO("Cannot open PEIL types")
        is PermissionDeniedItem -> TODO("Cannot open permission denied items")
        is Unknown -> UnknownTabContent(content)
    }
}