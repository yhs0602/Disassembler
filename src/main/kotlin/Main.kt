import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import storage.ProjectManager
import ui.FileDrawer
import viewmodel.MainViewModel
import javax.swing.JFileChooser
import javax.swing.SwingUtilities

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview
fun App() {
    var selectedZipFilePath by remember { mutableStateOf<String?>(null) }
    val mainViewModel = MainViewModel()

    MaterialTheme {
        Column {
            TopAppBar {
                Button(onClick = {
                    SwingUtilities.invokeLater {
                        val fileChooser = JFileChooser().apply {
                            fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                        }
                        val result = fileChooser.showOpenDialog(null)
                        if (result == JFileChooser.APPROVE_OPTION) {
                            selectedZipFilePath = fileChooser.selectedFile.absolutePath
                            mainViewModel.loadFiles(selectedZipFilePath!!)
                        }
                    }
                }) {
                    Text("Open Folder")
                }
                Button(onClick = {
                    ProjectManager.invalidateCache()
                }) {
                    Text("Invalidate Cache")
                }
            }
            Row(Modifier.fillMaxSize()) {
                FileDrawer(mainViewModel)
                Column(Modifier.fillMaxWidth()) {
                    // 세부 뷰 구현
                }
            }
        }
    }
}


fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Reverser") {
        App()
    }
}
