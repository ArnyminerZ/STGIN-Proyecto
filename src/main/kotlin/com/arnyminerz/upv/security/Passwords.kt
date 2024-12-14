package com.arnyminerz.upv.security

import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object Passwords {
    private const val ITERATIONS = 65536
    private const val KEY_LENGTH = 128

    fun hash(password: String): Pair<ByteArray, ByteArray> {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)

        val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")

        val hash = factory.generateSecret(spec).encoded
        return salt to hash
    }

    fun verify(salt: ByteArray, hash: ByteArray, password: String): Boolean {
        val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")

        val genHash = factory.generateSecret(spec).encoded
        return genHash.contentEquals(hash)
    }
}
