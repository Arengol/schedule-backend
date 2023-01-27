package ru.cchgeu.security.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import ru.cchgeu.security.TokenConfig
import java.util.*

class JwtTokenService(private val config: TokenConfig): TokenService {
    override fun generateTokenPair(vararg claims: TokenClaim): TokenPair {
        return TokenPair(
            accessToken = generateAccessToken(*claims),
            refreshToken = generateRefreshToken(*claims)
        )
    }

    private fun generateAccessToken(vararg claims: TokenClaim): String {
        var token = JWT.create()
            .withAudience(config.accessAudience)
            .withIssuer(config.issuer)
            .withExpiresAt(Date(System.currentTimeMillis() + config.accessExpiresIn))
        claims.forEach {claim ->
            token = token.withClaim(claim.name, claim.value)
        }
        return token.sign(Algorithm.HMAC256(config.secret))
    }

    private fun generateRefreshToken(vararg claims: TokenClaim): String {
        var token = JWT.create()
            .withAudience(config.refreshAudience)
            .withIssuer(config.issuer)
            .withExpiresAt(Date(System.currentTimeMillis() + config.refreshExpiresIn))
        claims.forEach {claim ->
            token = token.withClaim(claim.name, claim.value)
        }
        return token.sign(Algorithm.HMAC256(config.secret))
    }

}