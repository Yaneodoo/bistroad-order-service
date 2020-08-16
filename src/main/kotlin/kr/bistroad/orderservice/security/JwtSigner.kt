package kr.bistroad.orderservice.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.*

@Component
class JwtSigner(
    @Value("\${security.jwt.token.public-key}")
    publicKeyStr: String
) {
    private val publicKey: PublicKey

    init {
        val bytes = Base64.getDecoder().decode(
            publicKeyStr
                .replace("\n", "")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .toByteArray()
        )
        val keySpec = X509EncodedKeySpec(bytes)
        val keyFactory = KeyFactory.getInstance("RSA")

        publicKey = keyFactory.generatePublic(keySpec)
    }

    fun validateToken(jwt: String): Jws<Claims> {
        return Jwts.parserBuilder()
            .setSigningKey(publicKey)
            .build()
            .parseClaimsJws(jwt)
    }
}