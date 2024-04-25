package ui.treeitem

import com.kichik.pecoff4j.io.PEParser
import org.jetbrains.skia.Drawable
import ui.TreeNode
import util.isArchive
import java.io.File
import java.io.IOException

sealed class FileDrawerTreeItem(
    var caption: String,
    var level: Int,
    var tag: Any? = null,  // number or path or object
    var drawable: Drawable? = null
) : TreeNode<FileDrawerTreeItem> {
    abstract val isOpenable: Boolean

    companion object {
        fun fromFile(childLevel: Int, file: File?): FileDrawerTreeItem {
            return if (file == null) {
                EmptyItem(childLevel)
            } else if (file.isDirectory) {
                Folder(childLevel, file)
            } else {
                // get extension
                val lowerName = file.name.lowercase()
                val extension = file.extension.lowercase()
                val item: FileDrawerTreeItem? = when {
                    extension == "apk" -> {
                        APK(childLevel, file)
                    }

                    file.isArchive() -> {
                        Archive(childLevel, file)
                    }

                    lowerName.endsWith("assembly-csharp.dll") -> {
                        PEIL(childLevel, file)
                    }

                    extension == "dll" || extension == "exe" || extension == "sys" -> {
                        try {
                            val pe = PEParser.parse(file)
                            val idd = pe.optionalHeader.getDataDirectory(14)
                            // Check for CLR Runtime Header
                            if (idd.size != 0 && idd.virtualAddress != 0)
                                PEIL(childLevel, file)
                            else
                                PE(childLevel, file)
                        } catch (e: IOException) {
                            null
                        } catch (e: ArrayIndexOutOfBoundsException) {
                            null
                        } catch (e: NullPointerException) {
                            null
                        }
                    }

                    (extension == "so" ||
                        extension == "elf" ||
                        extension == "o" ||
                        extension == "bin" ||
                        extension == "axf" ||
                        extension == "prx" ||
                        extension == "puff" ||
                        extension == "ko" ||
                        extension == "mod"
                        ) -> {
                        Elf(childLevel, file)
                    }

                    extension == "dex" -> {
                        Dex(childLevel, file)
                    }

                    extension == "asm" -> {
                        Asm(childLevel, file)
                    }

                    else -> null
                }
                return item ?: Unknown(childLevel, file)
            }
        }
    }
}