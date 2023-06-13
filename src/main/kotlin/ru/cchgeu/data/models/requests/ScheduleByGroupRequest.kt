package ru.cchgeu.data.models.requests

import kotlinx.serialization.Serializable

@Serializable
data class ScheduleByGroupRequest (
    val groupName: String
)