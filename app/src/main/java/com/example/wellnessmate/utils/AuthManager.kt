package com.example.wellnessmate.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Patterns
import java.security.MessageDigest

class AuthManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("pulsepath_data", Context.MODE_PRIVATE)

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_LOGGED_IN, false)

    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)

    fun logout() {
        prefs.edit().putBoolean(KEY_LOGGED_IN, false).apply()
    }

    fun register(email: String, password: String): Result<Unit> {
        // Basic validations
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) return Result.failure(IllegalArgumentException("Invalid email"))
        if (password.length < 6) return Result.failure(IllegalArgumentException("Password must be at least 6 characters"))

        val existing = prefs.getString(KEY_USER_EMAIL, null)
        if (existing != null && existing.equals(email, ignoreCase = true)) {
            return Result.failure(IllegalStateException("User already exists"))
        }

        val hash = sha256(password)
        prefs.edit()
            .putString(KEY_USER_EMAIL, email)
            .putString(KEY_PASSWORD_HASH, hash)
            .putBoolean(KEY_LOGGED_IN, true)
            .apply()
        return Result.success(Unit)
    }

    fun login(email: String, password: String): Result<Unit> {
        val storedEmail = prefs.getString(KEY_USER_EMAIL, null)
        val storedHash = prefs.getString(KEY_PASSWORD_HASH, null)
        if (storedEmail.isNullOrBlank() || storedHash.isNullOrBlank()) {
            return Result.failure(IllegalStateException("No account found. Please register."))
        }
        if (!storedEmail.equals(email, ignoreCase = true)) {
            return Result.failure(IllegalArgumentException("Incorrect email or password"))
        }
        val hash = sha256(password)
        if (hash != storedHash) {
            return Result.failure(IllegalArgumentException("Incorrect email or password"))
        }
        prefs.edit().putBoolean(KEY_LOGGED_IN, true).apply()
        return Result.success(Unit)
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    companion object {
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_PASSWORD_HASH = "user_password_hash"
        private const val KEY_LOGGED_IN = "logged_in"
    }
}
