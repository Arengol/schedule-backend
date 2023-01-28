package ru.cchgeu.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.cchgeu.data.models.responses.GroupResponse

fun Route.getAllGroups(){
    authenticate {
        val response = mutableListOf<GroupResponse>()
        ru.cchgeu.data.selectAllGroups().forEach { response.add(GroupResponse(name = it.name)) }
        get("allGroups"){
            call.respond(
                status = HttpStatusCode.OK,
                message = response
            )
        }
    }
}