package ru.cchgeu.data

import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import ru.cchgeu.data.models.*


fun getAccountByUsername(userName: String): Account? {
    var account: Account? = null
    transaction {
        account = AccountTable.select { AccountTable.login eq userName }.limit(1).firstOrNull()?.ToAccount()
    }
    return account
}

fun getAccountById(userId: Int): Account? {
    var account: Account? = null
    transaction {
        account = AccountTable.select { AccountTable.id eq userId }.limit(1).firstOrNull()?.ToAccount()
    }
    return account
}

fun verifyInviteCode (inviteCode: String): Boolean {
    var result: Boolean = false
    transaction {
        result = InviteCodeTable.select {InviteCodeTable.code eq inviteCode}.limit(1).firstOrNull() != null
        if (result)
            InviteCodeTable.deleteWhere { InviteCodeTable.code eq inviteCode }
    }
    return result
}

fun createAccount(account: Account): Int? {
    var result: Int? = null
    transaction {
        if (AccountTable.select { AccountTable.login eq account.login }.limit(1).firstOrNull() == null) {
        result = AccountTable.insert {
                it[login] = account.login
                it[passwordHash] = account.passwordHash
                it[status] = account.status
                it[salt] = account.salt
            }.resultedValues!!.first()[AccountTable.id]
        }
    }
    return result
}

fun updateRefreshToken(accountId: Int, token: String) {
    transaction {
        AccountTable.update({ AccountTable.id eq accountId }) {
            it[refreshToken] = token
        }
    }
}

fun selectAllGroups(): List<Group> {
    val groups = mutableListOf<Group>()
    transaction {
        GroupTable.selectAll().forEach { groups.add(it.toGroup()) }
    }
    return groups
}

fun selectAllMentors(): List<Mentor> {
    val mentors = mutableListOf<Mentor>()
    transaction {
        MentorTable.selectAll().forEach { mentors.add(it.toMentor()) }
    }
    return mentors
}

fun createStudent(student: Student): Boolean {
    try {
        transaction {
            val accountId = StudentTable.select { StudentTable.account eq student.account }.limit(1).firstOrNull()
                ?.get(StudentTable.account)
            if (accountId != null) {
                StudentTable.deleteWhere { StudentTable.account eq accountId }
            }
            StudentTable.insert {
                it[groupName] = student.groupName
                it[account] = student.account
            }
        }
        return true
    }
    catch (e: ExposedSQLException){
        return false
    }
}

fun linkMentorAccount(mentor: Mentor): Boolean {
    try {
        transaction {
                MentorTable.update({MentorTable.name eq mentor.name}) {
                    it[MentorTable.account] = mentor.account
                }
        }
        return true
    }
    catch (e: ExposedSQLException) {
        return false
    }
}
fun selectGroupByAccountId(accountId: Int): String {
    var groupResult = ""
    transaction {
        groupResult = StudentTable.select { StudentTable.account eq accountId }.limit(1).first()[StudentTable.groupName]
    }
    return groupResult
}

fun selectMentorByAccountId(accountId: Int): String {
    var mentorResult = ""
    transaction {
        mentorResult = MentorTable.select { MentorTable.account eq accountId }.limit(1).first()[MentorTable.name]
    }
    return mentorResult
}

fun selectMentorSchedule(mentor: String): List<ScheduleData> {
    val scheduleResult = mutableListOf<ScheduleData>()
    transaction {
        (
                TimeScheduleTable innerJoin
                        ClassTable innerJoin
                        ClassAndGroupTable innerJoin
                        ClassAndMentorTable
                ).select { ClassAndMentorTable.mentor eq mentor and (ClassTable.accountId.isNull()) }.forEach { scheduleResult.add(ScheduleData(
                group = it[ClassAndGroupTable.group],
                time = it[TimeScheduleTable.time],
                dayWeek = it[TimeScheduleTable.dayOfWeek],
                weekType = it[TimeScheduleTable.weekType],
                name = it[ClassTable.name],
                classType = it[ClassTable.type],
                auditory = selectAuditoryByClassId(it[ClassTable.id]),
                mentor = selectMentorByClassId(it[ClassTable.id])
            )) }
    }
    return scheduleResult
}
fun selectScheduleByGroupId(groupId: String): List<ScheduleData> {
    val scheduleResult = mutableListOf<ScheduleData>()
    transaction {
        (
                TimeScheduleTable innerJoin
                        ClassTable innerJoin
                        ClassAndGroupTable
                ).select { ClassAndGroupTable.group eq groupId and (ClassTable.accountId.isNull()) }.forEach { scheduleResult.add(ScheduleData(
                group = it[ClassAndGroupTable.group],
                time = it[TimeScheduleTable.time],
                dayWeek = it[TimeScheduleTable.dayOfWeek],
                weekType = it[TimeScheduleTable.weekType],
                name = it[ClassTable.name],
                classType = it[ClassTable.type],
                auditory = selectAuditoryByClassId(it[ClassTable.id]),
                mentor = selectMentorByClassId(it[ClassTable.id])
                )) }
    }
    return scheduleResult
}

private fun selectMentorByClassId (classId: Int): List<String> {
    val mentorResult = mutableListOf<String>()
    transaction {
        (MentorTable innerJoin ClassAndMentorTable).select { ClassAndMentorTable.classId eq classId }
    }.forEach { mentorResult.add(it[MentorTable.name]) }
    return mentorResult
}

private fun selectAuditoryByClassId (classId: Int): List<String> {
    val mentorResult = mutableListOf<String>()
    transaction {
        (AuditoryTable innerJoin ClassAndAuditoryTable).select { ClassAndAuditoryTable.classId eq classId }
    }.forEach { mentorResult.add(it[AuditoryTable.name]) }
    return mentorResult
}

fun selectEventByAccountId (accountId: Int): List<Event> {
    val eventResult = mutableListOf<Event>()
    transaction {
        EventTable.select{EventTable.account eq accountId}.forEach { eventResult.add(it.ToEvent()) }
    }
    return eventResult
}

fun editEventById (event: Event) {
    transaction {
        EventTable.update({EventTable.id eq event.id!!}) {
            it[EventTable.timeBegin] = event.timeBegin
            it[EventTable.timeEnd] = event.timeEnd
            it[EventTable.date] = event.date
            it[EventTable.name] = event.name
        }
    }
}

fun deleteEventById (eventId: Int) {
    transaction {
        EventTable.deleteWhere { EventTable.id eq eventId }
    }
}

fun insertNewEvent (event: Event) {
    transaction {
        EventTable.insert {
            it[timeBegin] = event.timeBegin
            it[timeEnd] = event.timeEnd
            it[date] = event.date
            it[name] = event.name
            it[account] = event.account!!
        }
    }
}