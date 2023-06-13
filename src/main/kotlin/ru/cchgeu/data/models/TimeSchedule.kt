package ru.cchgeu.data.models

import org.jetbrains.exposed.sql.Table

object TimeScheduleTable : Table("time_schedule") {
    val id = integer("id").autoIncrement()
    val time = varchar("time", 15)
    val dayOfWeek = varchar("day_week", 11)
    val weekType = integer("week_type")
    override val primaryKey = PrimaryKey(id, name = "time_schedule_pk")
}