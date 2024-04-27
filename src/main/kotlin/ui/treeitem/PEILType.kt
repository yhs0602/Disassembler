package ui.treeitem

import at.pollaknet.api.facile.FacileReflector
import at.pollaknet.api.facile.symtab.symbols.Type
import util.getValueFromTypeKindAndBytes

class PEILType(
    level: Int,
    caption: String,
    private val facileReflector: FacileReflector,
    private val type: Type
) : FileDrawerTreeItem(caption, level) {
    override val isOpenable: Boolean = false

    override fun isExpandable(): Boolean = true

    override fun getChildren(): List<FileDrawerTreeItem> {
        val items = mutableListOf<FileDrawerTreeItem>()
        val fields = type.fields
        val methods = type.methods
        for (field in fields) {
            val c = field.constant
            var fieldDesc = field.name + " : " + field.typeRef.name
            if (c != null) {
                val kind = c.elementTypeKind
                val bytes = c.value
                val value = getValueFromTypeKindAndBytes(bytes, kind)
                fieldDesc += "(=${value})"
            } else {
                println("Field: $fieldDesc")
            }
            items.add(
                PEILField(
                    level + 1,
                    fieldDesc
                )
            )
        }
        for (method in methods) {
            val caption = method.name + method.methodSignature
            items.add(
                PEILMethod(
                    level + 1,
                    caption,
                    facileReflector,
                    method
                )
            )
        }
        return items
    }
}