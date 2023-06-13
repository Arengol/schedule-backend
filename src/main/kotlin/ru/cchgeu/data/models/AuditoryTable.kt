package ru.cchgeu.data.models

import org.jetbrains.exposed.sql.Table

object AuditoryTable : Table("auditory") {
    val name = varchar("name", 10)
    override val primaryKey = PrimaryKey(name, name = "auditory_pk")
}