import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

@Composable
@Preview
fun App() {
    var selectedZipFilePath by remember { mutableStateOf<String?>(null) }
    var zipContent by remember { mutableStateOf<List<String>>(listOf()) }

    MaterialTheme {
        Column {
            TopAppBar {
                Button(onClick = {
                    // 파일 선택 로직 구현
                }) {
                    Text("파일 추가")
                }
            }
            Row(Modifier.fillMaxSize()) {
                LazyColumn {
                    // 트리 뷰 구현
                }
                Column(Modifier.fillMaxWidth()) {
                    // 세부 뷰 구현
                }
            }
        }
    }
}


fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
