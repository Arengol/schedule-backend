package ru.cchgeu.data.models

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object InviteCodeTable: Table("invite_code") {
    val code = varchar("code", 25)
    override val primaryKey = PrimaryKey(code, name = "invite_code_pk")
}

data class InviteCode (
    val code: String
)

fun ResultRow.ToInviteCode () = InviteCode(
    code = this[InviteCodeTable.code]
)