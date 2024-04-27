package util

import at.pollaknet.api.facile.symtab.TypeKind
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.experimental.and

fun getValueFromTypeKindAndBytes(bytes: ByteArray, kind: Int): Any {
    val bb = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
    return when (kind) {
        TypeKind.ELEMENT_TYPE_BOOLEAN -> bytes[0].toInt() != 0
        TypeKind.ELEMENT_TYPE_CHAR -> bytes[0].toInt().toChar()
        TypeKind.ELEMENT_TYPE_I -> bb.int
        TypeKind.ELEMENT_TYPE_I1 -> bb.get()
        TypeKind.ELEMENT_TYPE_I2 -> bb.short
        TypeKind.ELEMENT_TYPE_I4 -> bb.int
        TypeKind.ELEMENT_TYPE_I8 -> bb.long
        TypeKind.ELEMENT_TYPE_U -> bb.long
        TypeKind.ELEMENT_TYPE_U1 -> bb.get() and 0xFF.toByte()
        TypeKind.ELEMENT_TYPE_U2 -> bb.short and 0xFFFF.toShort()
        TypeKind.ELEMENT_TYPE_U4 -> bb.int
        TypeKind.ELEMENT_TYPE_U8 -> bb.long
        TypeKind.ELEMENT_TYPE_R4 -> bb.float
        TypeKind.ELEMENT_TYPE_R8 -> bb.double
        TypeKind.ELEMENT_TYPE_STRING -> String(bytes)
        else -> "Unknown!!!!"
    }
}