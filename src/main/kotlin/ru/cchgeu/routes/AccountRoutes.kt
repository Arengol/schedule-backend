package ru.cchgeu.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.cchgeu.data.*
import ru.cchgeu.data.models.Event
import ru.cchgeu.data.models.Mentor
import ru.cchgeu.data.models.Student
import ru.cchgeu.data.models.requests.*
import ru.cchgeu.data.models.responses.EventResponse
import ru.cchgeu.data.models.responses.GroupResponse
import ru.cchgeu.data.models.responses.MentorResponse
import ru.cchgeu.data.models.responses.ScheduleResponse

fun Route.getAllGroups(){
        val response = mutableListOf<GroupResponse>()
        selectAllGroups().forEach { response.add(GroupResponse(name = it.name)) }
        get("allGroups"){
            call.respond(
                status = HttpStatusCode.OK,
                message = response
            )
        }
}

fun Route.getAllMentors() {
        val response = mutableListOf<MentorResponse>()
        selectAllMentors().forEach { response.add(MentorResponse(name = it.name)) }
        get("allMentors"){
            call.respond(
                status = HttpStatusCode.OK,
                message = response
            )
        }
}

fun Route.getScheduleByGroup() {
    authenticate {
        post("schedulebygroup") {
            val response = mutableListOf<ScheduleResponse>()
            val request = call.receiveNullable<ScheduleByGroupRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val areFieldsBlank = request.groupName.isBlank()
            if (areFieldsBlank) {
                call.respond(HttpStatusCode.Conflict)
                return@post
            }
            selectScheduleByGroupId(request.groupName).forEach{response.add(ScheduleResponse(
                group = "",
                time = it.time,
                dayWeek = it.dayWeek,
                weekType = it.weekType,
                name = it.name,
                classType = it.classType,
                auditory = it.auditory,
                mentor = it.mentor))
            }
            call.respond(
                status = HttpStatusCode.OK,
                message = response
            )
        }
    }
}

fun Route.getScheduleByUserId() {
    authenticate {
        get("schedulebyaccountid") {
            val response = mutableListOf<ScheduleResponse>()
            val principal = call.principal<JWTPrincipal>()
            val tokenUserId = principal?.getClaim("userID", String::class)
            val account = getAccountById(tokenUserId!!.toInt())
            if (account!!.status == 0) {
                val studentGroup = selectGroupByAccountId(tokenUserId!!.toInt())
                selectScheduleByGroupId(studentGroup).forEach {
                    response.add(
                        ScheduleResponse(
                            group = "",
                            time = it.time,
                            dayWeek = it.dayWeek,
                            weekType = it.weekType,
                            name = it.name,
                            classType = it.classType,
                            auditory = it.auditory,
                            mentor = it.mentor
                        )
                    )
                }
            }
            else {
                val mentorName = selectMentorByAccountId(tokenUserId!!.toInt())
                selectMentorSchedule(mentorName).forEach {response.add(ScheduleResponse(
                    mentor = null,
                    group = it.group,
                    time = it.time,
                    dayWeek = it.dayWeek,
                    weekType = it.weekType,
                    name = it.name,
                    classType = it.classType,
                    auditory = it.auditory))
                }
            }
            call.respond(
                status = HttpStatusCode.OK,
                message = response
            )
        }
    }
}

//fun Route.getMentorScheduleById() {
//    authenticate {
//        get ("mentorshedulebyid") {
//            val response = mutableListOf<ScheduleResponse>()
//            val principal = call.principal<JWTPrincipal>()
//            val tokenUserId = principal?.getClaim("userID", String::class)
//            val mentorName = selectMentorByAccountId(tokenUserId!!.toInt())
//            selectMentorSchedule(mentorName).forEach {response.add(ScheduleResponse(
//                mentor = null,
//                group = it.group,
//                time = it.time,
//                dayWeek = it.dayWeek,
//                weekType = it.weekType,
//                name = it.name,
//                classType = it.classType,
//                auditory = it.auditory))
//            }
//            call.respond(
//                status = HttpStatusCode.OK,
//                message = response
//            )
//        }
//    }
//}
fun Route.getMentorScheduleByName() {
    authenticate {
        post ("mentorschedulebyname") {
            val response = mutableListOf<ScheduleResponse>()
            val request = call.receiveNullable<MentorScheduleByNameRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val areFieldsBlank = request.mentor.isBlank()
            if (areFieldsBlank) {
                call.respond(HttpStatusCode.Conflict)
                return@post
            }
            selectMentorSchedule(request.mentor).forEach {response.add(ScheduleResponse(
                mentor = null,
                group = it.group,
                time = it.time,
                dayWeek = it.dayWeek,
                weekType = it.weekType,
                name = it.name,
                classType = it.classType,
                auditory = it.auditory))
            }
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

fun Route.addNewEvent() {
    authenticate {
        post("event") {
            val principal = call.principal<JWTPrincipal>()
            val tokenUserId = principal?.getClaim("userID", String::class)
            val request = call.receiveNullable<AddNewEventRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            insertNewEvent(Event(
                timeBegin = request.timeBegin,
                timeEnd = request.timeEnd,
                date = request.date,
                name = request.name,
                account = tokenUserId!!.toInt(),
                id = null
            ))
            call.respond(HttpStatusCode.OK)
        }
    }
}

fun Route.getEventsByUserId() {
    authenticate {
        get("event") {
            val response = mutableListOf<EventResponse>()
            val principal = call.principal<JWTPrincipal>()
            val tokenUserId = principal?.getClaim("userID", String::class)
            selectEventByAccountId(tokenUserId!!.toInt()).forEach{response.add(
                EventResponse(
                id = it.id!!,
                timeBegin = it.timeBegin,
                timeEnd = it.timeEnd,
                date = it.date,
                name = it.name
            )
            )}
            call.respond(
                status = HttpStatusCode.OK,
                message = response
            )
        }
    }
}

fun Route.deleteEvent() {
    authenticate {
        delete("event") {
            val request = call.receiveNullable<DeleteEventByIdRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }
            deleteEventById(request.eventId)
            call.respond(HttpStatusCode.OK)
        }
    }
}

fun Route.editEvent() {
    authenticate {
        patch("event") {
            val request = call.receiveNullable<UpdateEventByIdRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@patch
            }
            editEventById(
                Event(
                id = request.id,
                timeBegin = request.timeBegin,
                timeEnd = request.timeEnd,
                date = request.date,
                name = request.name,
                account = null
                )
            )
            call.respond(HttpStatusCode.OK)
        }
    }
}

fun Route.editSchedule() {
    authenticate {
        get("schedulebyaccountid") {

        }
    }
}



