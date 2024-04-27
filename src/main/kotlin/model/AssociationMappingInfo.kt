package model

data class AssociationMappingInfo(
    val pathToTabKind: MutableMap<String, String>, // File path to TabKind name
    val pathToFileDrawerTreeItem: MutableMap<String, String>, // File path to FileDrawerTreeItem name
)
