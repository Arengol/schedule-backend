package ru.cchgeu.data.models

import org.jetbrains.exposed.sql.Table

object Account : Table("accounts") {
    val id = integer("id").autoIncrement()
    val login = varchar("login", 20)
    val passwordHash = varchar("password_hash", 32)
    val status = integer("status")
    val token = varchar("refresh_token", 50)
    val salt = varchar("salt", 32)
    override val primaryKey = PrimaryKey(id, name = "accounts_pk")
}