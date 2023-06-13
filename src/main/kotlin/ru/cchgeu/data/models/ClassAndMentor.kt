package ru.cchgeu.data.models

import org.jetbrains.exposed.sql.Table

object ClassAndMentorTable : Table("class_and_mentor") {
    val classId = integer("class_id").references(ClassTable.id)
    val mentor = varchar("mentor_name", 40).references(MentorTable.name)
}