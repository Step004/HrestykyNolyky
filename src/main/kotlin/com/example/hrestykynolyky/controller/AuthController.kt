package com.example.hrestykynolyky.controller

import com.example.hrestykynolyky.data.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(@Autowired private val jdbcTemplate: JdbcTemplate) {

    data class SignUpRequest(
        val username: String,
        val password: String
    )

    data class LoginRequest(
        val username: String,
        val password: String
    )

    data class LoginResponse(
        val success: Boolean,
        val message: String,
        val username: String? = null
    )

    data class User(
        val id: Int,
        val username: String,
        val password: String
    )

    data class UserResponse(
        val username: String
    )

    @PostMapping("/signup")
    fun signUp(@RequestBody signUpRequest: SignUpRequest): ResponseEntity<UserResponse> {
        // Перевіряємо чи існує користувач з таким username
        val userExists = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM UserKhrestyky WHERE username = ?",
            arrayOf(signUpRequest.username),
            Integer::class.java
        )

        if (userExists != null && userExists > 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(UserResponse(username = signUpRequest.username))
        }

        // Зберігаємо користувача в базу даних
        jdbcTemplate.update(
            "INSERT INTO UserKhrestyky(username, password) VALUES (?, ?)",
            signUpRequest.username, signUpRequest.password
        )

        // Повертаємо дані про створеного користувача
        val userResponse = UserResponse(username = signUpRequest.username)
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse)
    }

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<LoginResponse> {
        val user = jdbcTemplate.queryForObject(
            "SELECT * FROM UserKhrestyky WHERE username = ?",
            arrayOf(loginRequest.username)
        ) { rs, _ ->
            User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password")
            )
        }

        return if (user != null && loginRequest.password == user.password) {
            ResponseEntity.ok(
                LoginResponse(
                    success = true,
                    message = "Login successful",
                    username = user.username
                )
            )
        } else {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                LoginResponse(
                    success = false,
                    message = "Invalid credentials"
                )
            )
        }
    }
}
