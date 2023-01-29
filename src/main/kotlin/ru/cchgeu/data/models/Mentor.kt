package ru.cchgeu.data.models

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object MentorTable : Table("mentor") {
    val name = varchar("name", 40)
    val account = integer("account_id").references(AccountTable.id)
    override val primaryKey = PrimaryKey(name, name = "mentor_pk")
}

data class Mentor (
    val name: String
)

fun ResultRow.toMentor() = Mentor(
    name = this[MentorTable.name]
)