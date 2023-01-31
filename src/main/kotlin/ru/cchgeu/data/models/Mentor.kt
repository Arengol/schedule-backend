package ru.cchgeu.data.models

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object MentorTable : Table("mentor") {
    val name = varchar("name", 40)
    val account = integer("account_id").references(AccountTable.id).nullable()
    override val primaryKey = PrimaryKey(name, name = "mentor_pk")
}

data class Mentor (
    val name: String,
    val account: Int
)

fun ResultRow.toMentor() = if (this[MentorTable.account] != null)
    Mentor(
        name = this[MentorTable.name],
        account = this[MentorTable.account]!!
    )
else
    Mentor(
        name = this[MentorTable.name],
        account = 0
    )
