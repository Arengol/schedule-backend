package ru.cchgeu.security.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

class JwtTokenService(
    private val issuer: String,
    private val accessAudience: String,
    private val accessExpiresIn: Long,
    private val refreshAudience: String,
    private val refreshExpiresIn: Long,
    private val secret: String): TokenService {
    override fun generateTokenPair(vararg claims: TokenClaim): TokenPair {
        return TokenPair(
            accessToken = generateAccessToken(*claims),
            refreshToken = generateRefreshToken(*claims)
        )
    }

    private fun generateAccessToken(vararg claims: TokenClaim): String {
        var token = JWT.create()
            .withAudience(accessAudience)
            .withIssuer(issuer)
            .withExpiresAt(Date(System.currentTimeMillis() + accessExpiresIn))
        claims.forEach {claim ->
            token = token.withClaim(claim.name, claim.value)
        }
        return token.sign(Algorithm.HMAC256(secret))
    }

    private fun generateRefreshToken(vararg claims: TokenClaim): String {
        var token = JWT.create()
            .withAudience(refreshAudience)
            .withIssuer(issuer)
            .withExpiresAt(Date(System.currentTimeMillis() + refreshExpiresIn))
        claims.forEach {claim ->
            token = token.withClaim(claim.name, claim.value)
        }
        return token.sign(Algorithm.HMAC256(secret))
    }

}