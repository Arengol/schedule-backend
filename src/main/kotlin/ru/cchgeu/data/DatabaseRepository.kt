package ru.cchgeu.data

import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.postgresql.util.PSQLException
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