package ru.cchgeu.data.models.requests

import kotlinx.serialization.Serializable

@Serializable
data class AssociateGroupRequest(
    val userId: Int,
    val groupName: String
)
