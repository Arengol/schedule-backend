package ru.cchgeu.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.cchgeu.data.createAccount
import ru.cchgeu.data.getAccountById
import ru.cchgeu.data.getAccountByUsername
import ru.cchgeu.data.models.Account
import ru.cchgeu.data.models.requests.SignUpRequest
import ru.cchgeu.data.models.requests.RefreshRequest
import ru.cchgeu.data.models.requests.SignInRequest
import ru.cchgeu.data.models.responses.AuthResponse
import ru.cchgeu.data.updateRefreshToken
import ru.cchgeu.security.hashing.SHA256HashingService
import ru.cchgeu.security.hashing.SaltedHash
import ru.cchgeu.security.token.TokenClaim
import ru.cchgeu.security.token.TokenService

fun Route.signUp(hashingService: SHA256HashingService, tokenService: TokenService) {
    post("signup"){
        val request = call.receiveNullable<SignUpRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val areFieldsBlank = request.username.isBlank() || request.password.isBlank()
        val isPasswordShort = request.password.length < 8
        val isUserNameLong = request.username.length >= 20
        if (areFieldsBlank || isPasswordShort || isUserNameLong) {
            call.respond(HttpStatusCode.Conflict)
            return@post
        }

        val saltedHash = hashingService.generateSaltedHash(request.password)
        val account = Account(
            id = null,
            login = request.username,
            passwordHash = saltedHash.hash,
            salt = saltedHash.salt,
            status = request.status,
            refreshToken = null
        )
        val accountId = createAccount(account) ?: kotlin.run {
            call.respond(HttpStatusCode.Conflict, "Username is already exists")
            return@post
        }
        val tokenPair = tokenService.generateTokenPair(
            TokenClaim(
                name = "userID",
                value = accountId.toString()
            )
        )
        updateRefreshToken(accountId, tokenPair.refreshToken)
        call.respond(
            status = HttpStatusCode.OK,
            message = AuthResponse(
                userId = accountId,
                accessToken = tokenPair.accessToken,
                refreshToken = tokenPair.refreshToken
            )
        )
    }
}

fun Route.signIn(hashingService: SHA256HashingService, tokenService: TokenService) {
    post("signin"){
        val request = call.receiveNullable<SignInRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val areFieldsBlank = request.username.isBlank() || request.password.isBlank()
        val isPasswordShort = request.password.length < 8
        val isUserNameLong = request.username.length >= 20
        if (areFieldsBlank || isPasswordShort || isUserNameLong) {
            call.respond(HttpStatusCode.Conflict)
            return@post
        }

        val account = getAccountByUsername(request.username) ?: kotlin.run {
            call.respond(HttpStatusCode.Unauthorized)
            return@post
        }
        if (hashingService.verify(request.password, SaltedHash(account.passwordHash, account.salt))) {
            val tokenPair = tokenService.generateTokenPair(
                TokenClaim(
                    name = "userID",
                    value = account.id.toString()
                )
            )
            updateRefreshToken(account.id!!, tokenPair.refreshToken)
            call.respond(
                status = HttpStatusCode.OK,
                message = AuthResponse(
                    userId = account.id,
                    accessToken = tokenPair.accessToken,
                    refreshToken = tokenPair.refreshToken
                )
            )
        }
        else {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
}

fun Route.refresh(hashingService: SHA256HashingService, tokenService: TokenService) {
    post("refresh"){
        val request = call.receiveNullable<RefreshRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val account = getAccountById(request.userId) ?: kotlin.run {
            call.respond(HttpStatusCode.Unauthorized)
            return@post
        }

        if (!account.refreshToken!!.equals(request.refreshToken)){
            call.respond(HttpStatusCode.Unauthorized)
            return@post
        }

        val tokenPair = tokenService.generateTokenPair(
            TokenClaim(
                name = "userID",
                value = request.userId.toString()
            )
        )

        updateRefreshToken(request.userId, tokenPair.refreshToken)
        call.respond(
            status = HttpStatusCode.OK,
            message = AuthResponse(
                userId = request.userId,
                accessToken = tokenPair.accessToken,
                refreshToken = tokenPair.refreshToken
            )
        )
    }
}
