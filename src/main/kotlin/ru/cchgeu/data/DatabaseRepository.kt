package ru.cchgeu.data

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
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