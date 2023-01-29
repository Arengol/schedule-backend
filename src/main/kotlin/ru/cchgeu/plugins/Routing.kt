package ru.cchgeu.plugins

import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import ru.cchgeu.routes.*
import ru.cchgeu.security.hashing.SHA256HashingService
import ru.cchgeu.security.token.TokenService

fun Application.configureRouting(
    hashingService: SHA256HashingService,
    tokenService: TokenService
) {
    routing {
        signUp(hashingService, tokenService)
        signIn(hashingService, tokenService)
        refresh(hashingService, tokenService)
        getAllGroups()
        getAllMentors()
    }
}
