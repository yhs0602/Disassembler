package ui.treeitem

object FileNameComparator : Comparator<FileDrawerTreeItem> {
    override fun compare(
        p1: FileDrawerTreeItem,
        p2: FileDrawerTreeItem
    ): Int {
        val cdir = compareDir(p1, p2)
        return if (cdir == 0) {
            if (p1.caption.endsWith("/")) {
                if (p1.caption == "/") {
                    return -1
                }
                if (p2.caption == "/") {
                    return 1
                }
                if (p1.caption == "../") {
                    return -1
                }
                if (p2.caption == "../") {
                    1
                } else p1.caption.compareTo(p2.caption)
            } else {
                p1.caption.compareTo(p2.caption)
            }
        } else {
            cdir
        }
    }

    private fun compareDir(
        p1: FileDrawerTreeItem,
        p2: FileDrawerTreeItem
    ): Int {
        if (p1.caption.endsWith("/")) {
            return if (p2.caption.endsWith("/")) {
                0
            } else {
                -1
            }
        } else if (p2.caption.endsWith("/")) {
            return 1
        }
        return p1.caption.compareTo(p2.caption)
    }
}