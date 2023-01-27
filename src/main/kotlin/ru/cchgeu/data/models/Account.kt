package ru.cchgeu.data.models

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object AccountTable : Table("accounts") {
    val id = integer("id").autoIncrement()
    val login = varchar("login", 20)
    val passwordHash = varchar("password_hash", 64)
    val status = integer("status")
    val refreshToken = varchar("refresh_token", 200)
    val salt = varchar("salt", 64)
    override val primaryKey = PrimaryKey(id, name = "accounts_pk")
}

data class Account(
    val id: Int?,
    val login: String,
    val passwordHash: String,
    val status: Int,
    val refreshToken: String?,
    val salt: String
)

fun ResultRow.ToAccount () = Account(
    id = this[AccountTable.id],
    login = this[AccountTable.login],
    passwordHash = this[AccountTable.passwordHash],
    status = this[AccountTable.status],
    refreshToken = this[AccountTable.refreshToken],
    salt = this[AccountTable.salt]
)