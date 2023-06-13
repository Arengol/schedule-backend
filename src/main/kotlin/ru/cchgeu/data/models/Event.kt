package ru.cchgeu.data.models

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import ru.cchgeu.data.models.EventTable.autoIncrement
import ru.cchgeu.data.models.EventTable.nullable
import ru.cchgeu.data.models.EventTable.references

object EventTable : Table("event") {
    val id = integer("id").autoIncrement()
    val timeBegin = varchar("time_begin", 15)
    val timeEnd = varchar("time_end", 15)
    val date = varchar("date", 15)
    val name = varchar("name", 50)
    val account = integer("account_id").references(AccountTable.id)
    override val primaryKey = PrimaryKey(id, name = "event_pk")
}

data class Event(
    val id: Int?,
    val timeBegin: String,
    val timeEnd: String,
    val date: String,
    val name: String,
    val account: Int?
)

fun ResultRow.ToEvent () = Event(
    id = this[EventTable.id],
    timeBegin = this[EventTable.timeBegin],
    timeEnd = this[EventTable.timeEnd],
    date = this[EventTable.date],
    name = this[EventTable.name],
    account = this[EventTable.account]
)