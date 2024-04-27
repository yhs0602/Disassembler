package ui.tab.tabkind

import java.io.File


sealed class TabKind(val caption: String) {
    class AnalysisResult(val file: File) : TabKind("Analysis Result")
    class Binary(val file: File) : TabKind(file.name)
    class Image(val file: File) : TabKind(file.name)
    class Text(val file: File) : TabKind(file.name)
    class Hex(val file: File) : TabKind(file.name) // Special
    data object Log : TabKind("Log")
    class FoundString(val relPath: String, val range: IntRange) : TabKind("Found String")
}