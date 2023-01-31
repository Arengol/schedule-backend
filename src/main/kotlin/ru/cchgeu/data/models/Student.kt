package ru.cchgeu.data.models

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object StudentTable : Table("student") {
    val groupName = varchar("student_group_name", 10).references(GroupTable.name)
    val account = integer("account_id").references(AccountTable.id)
}

data class Student (
    val groupName: String,
    val account: Int
)

fun ResultRow.toStudent () = Student(
    groupName = this[GroupTable.name],
    account = this[AccountTable.id]
)