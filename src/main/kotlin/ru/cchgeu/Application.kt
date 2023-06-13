package ru.cchgeu

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import ru.cchgeu.data.createAccount
import ru.cchgeu.data.models.Account
import ru.cchgeu.data.selectScheduleByGroupId
import ru.cchgeu.plugins.*
import ru.cchgeu.security.TokenConfig
import ru.cchgeu.security.hashing.*
import ru.cchgeu.security.token.JwtTokenService

//fun main(args: Array<String>){
//    Database.connect("jdbc:postgresql://localhost:5432/schedule", driver = "org.postgresql.Driver",
//        user = "admin", password = "h9Zhugku4CtxQksWaJ3BmpLF")
//    val m = selectScheduleByGroupId("СУЗ-221")
//    println("Ok")
//}
fun main(args: Array<String>): Unit =
    io.ktor.server.cio.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    Database.connect(url = environment.config.property("database.url").getString(),
        driver = "org.postgresql.Driver",
        user = environment.config.property("database.user").getString(),
        password = environment.config.property("database.password").getString())
    val hashingService = SHA256HashingService()
    val tokenConfig = TokenConfig(
        environment.config.property("jwt.issuer").getString(),
        environment.config.property("jwt.accessAudience").getString(),
        environment.config.property("jwt.accessExpire").getString().toLong(),
        environment.config.property("jwt.refreshAudience").getString(),
        environment.config.property("jwt.refreshExpire").getString().toLong(),
        environment.config.property("jwt.secret").getString()
    )
    val tokenService = JwtTokenService(tokenConfig)
    configureSecurity(tokenConfig)
    configureSerialization()
    configureRouting(hashingService, tokenService)
}
