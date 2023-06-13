package ru.cchgeu.data.models

import org.jetbrains.exposed.sql.Table

object ClassAndAuditoryTable : Table("class_and_auditory") {
    val classId = integer("class_id").references(ClassTable.id)
    val auditory = varchar("auditory_name", 10).references(AuditoryTable.name)
}