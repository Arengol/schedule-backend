package ru.cchgeu.security.token

interface TokenService {
    fun generateTokenPair(vararg claims: TokenClaim): TokenPair
}