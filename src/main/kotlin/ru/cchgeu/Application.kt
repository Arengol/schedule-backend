package ru.cchgeu

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import ru.cchgeu.plugins.*
import ru.cchgeu.security.hashing.*
import ru.cchgeu.security.token.JwtTokenService

fun main(args: Array<String>){
    val service = SHA256HashingService()
    val saltedHash = service.generateSaltedHash("132")
    val valid = service.verify("132", saltedHash)

    println(valid)
}
//fun main(args: Array<String>): Unit =
//    io.ktor.server.cio.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    Database.connect(url = environment.config.property("database.url").getString(),
        driver = "org.postgresql.Driver",
        user = environment.config.property("database.user").getString(),
        password = environment.config.property("database.password").getString())
    configureSecurity()
    configureSerialization()
    configureRouting()
}
