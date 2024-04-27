package ui.treeitem

import org.apache.tika.config.TikaConfig
import org.apache.tika.metadata.Metadata
import ui.tab.tabkind.FileExtensions
import ui.tab.tabkind.TabKind
import ui.tab.tabkind.TabKind.*
import java.io.File

class Unknown(level: Int, val file: File) : FileDrawerTreeItem(file.name, level, drawable = "unknown.png"), Openable {

    override fun isExpandable(): Boolean = false

    override fun getChildren(): List<FileDrawerTreeItem> = TODO("Not supported")

    override fun toTabKind(): TabKind {
        val extension = file.extension
        if (extension in FileExtensions.textFileExts) {
            return Text(file)
        } else {
            val tika = TikaConfig()
            val metadata = Metadata()
            file.inputStream().use {
                val mimeType = tika.detector.detect(it, metadata)
                if (mimeType.type == "image") {
                    return Image(file)
                }
            }
            return Binary(file)
        }
    }
}