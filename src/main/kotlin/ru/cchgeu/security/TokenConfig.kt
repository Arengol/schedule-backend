package ru.cchgeu.security

class TokenConfig(
     val issuer: String,
     val accessAudience: String,
     val accessExpiresIn: Long,
     val refreshAudience: String,
     val refreshExpiresIn: Long,
     val secret: String
)