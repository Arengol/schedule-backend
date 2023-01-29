package ru.cchgeu.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.cchgeu.data.models.responses.GroupResponse
import ru.cchgeu.data.models.responses.MentorResponse
import ru.cchgeu.data.selectAllGroups
import ru.cchgeu.data.selectAllMentors

fun Route.getAllGroups(){
    authenticate {
        val response = mutableListOf<GroupResponse>()
        selectAllGroups().forEach { response.add(GroupResponse(name = it.name)) }
        get("allGroups"){
            call.respond(
                status = HttpStatusCode.OK,
                message = response
            )
        }
    }
}

fun Route.getAllMentors(){
    authenticate {
        val response = mutableListOf<MentorResponse>()
        selectAllMentors().forEach { response.add(MentorResponse(name = it.name)) }
        get("allMentors"){
            call.respond(
                status = HttpStatusCode.OK,
                message = response
            )
        }
    }
}