package ru.cchgeu.data.models

import org.jetbrains.exposed.sql.Table

object ClassAndGroupTable : Table("class_and_group") {
    val classId = integer("class_id").references(ClassTable.id)
    val group = varchar("student_group_name", 10).references(GroupTable.name)
}