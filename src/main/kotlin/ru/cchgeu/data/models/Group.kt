package ru.cchgeu.data.models

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object GroupTable : Table("student_group") {
    val name = varchar( "name",10)
    override val primaryKey = PrimaryKey(name, name = "group_pk")
}

data class Group (
    val name: String
)

fun ResultRow.toGroup() = Group(
    name = this[GroupTable.name]
)