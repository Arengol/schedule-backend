package ru.cchgeu.data.models

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object ClassTable : Table("class") {
    val name = varchar("name", 250)
    val type = varchar("type",8)
    val timeSchedule = integer("time_schedule_id").references(TimeScheduleTable.id)
    val id = integer("id").autoIncrement()
    val accountId = integer("account_id").references(AccountTable.id)
    override val primaryKey = PrimaryKey(id, name = "class_pk")
}

data class ScheduleData (
    val group: String,
    val time: String,
    val dayWeek: String,
    val weekType: Int,
    val name: String,
    val classType: String,
    val auditory: List<String>?,
    val mentor: List<String>?
)

//fun ResultRow.toScheduleData() = ScheduleData (
//    group = this[GroupTable.name],
//    time = this[TimeScheduleTable.time],
//    dayWeek = this[TimeScheduleTable.dayOfWeek],
//    weekType = this[TimeScheduleTable.weekType],
//    name = this[ClassTable.name],
//
//)

