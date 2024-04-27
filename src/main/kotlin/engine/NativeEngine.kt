package engine

// Use capstone library, taken from Android-Disassembler
class NativeEngine {
    external fun getSome(
        handle: Int,
        bytes: ByteArray,
        offset: Long,
        size: Long,
        virtaddr: Long,
        count: Int /*,ArrayList<ListViewItem> arr*/
    ): Long

    external fun CSoption(handle: Int, type: Int, vslue: Int): Int
}