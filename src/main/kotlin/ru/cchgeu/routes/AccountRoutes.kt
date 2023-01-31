package ru.cchgeu.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.cchgeu.data.*
import ru.cchgeu.data.models.Mentor
import ru.cchgeu.data.models.Student
import ru.cchgeu.data.models.requests.AssociateGroupRequest
import ru.cchgeu.data.models.requests.AssociateMentorRequest
import ru.cchgeu.data.models.requests.RefreshRequest
import ru.cchgeu.data.models.responses.GroupResponse
import ru.cchgeu.data.models.responses.MentorResponse

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

fun Route.getAllMentors() {
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

fun Route.associateGroup() {
    authenticate {
        post("associategroup") {
            val principal = call.principal<JWTPrincipal>()
            val tokenUserId = principal?.getClaim("userID", String::class)
            val request = call.receiveNullable<AssociateGroupRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            if (tokenUserId!!.toInt() != request.userId){
                call.respond(HttpStatusCode.Forbidden)
                return@post
            }
            if(createStudent(Student(
                groupName = request.groupName,
                account = request.userId
            )))
            call.respond(HttpStatusCode.OK)
            else call.respond(HttpStatusCode.Conflict)
        }
    }
}

fun Route.associateMentor() {
    authenticate {
        post("associatementor") {
            val principal = call.principal<JWTPrincipal>()
            val tokenUserId = principal?.getClaim("userID", String::class)
            val request = call.receiveNullable<AssociateMentorRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val account = getAccountById(request.userId)
            if (tokenUserId!!.toInt() != request.userId || account!!.status != 1){
                call.respond(HttpStatusCode.Forbidden)
                return@post
            }
            linkMentorAccount(Mentor(
                account = request.userId,
                name = request.mentorName
            ))
            call.respond(HttpStatusCode.OK)
        }
    }
}