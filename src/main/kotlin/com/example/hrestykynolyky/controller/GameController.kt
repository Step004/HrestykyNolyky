package com.example.hrestykynolyky.controller

import com.example.hrestykynolyky.data.Game
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/game")
class GameController(private val jdbcTemplate: JdbcTemplate) {
    data class Game(
        val username: String,
        val time: Int,
        val victory: Int,
        val allGames: Int
    )

    @PostMapping
    fun createGame(@RequestBody game: Game): ResponseEntity<Game> {
        jdbcTemplate.update(
            "INSERT INTO games (username, time, victory, allGames) VALUES (?, ?, ?,?)",
            game.username, game.time, game.victory, game.allGames
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(game)
    }

    @PutMapping("/{username}")
    fun updateGame(@PathVariable username: String, @RequestBody updatedGame: Game): ResponseEntity<Game> {
        val existingGame = findGameByUsername(username) ?: throw RuntimeException("Game not found")
        jdbcTemplate.update(
            "UPDATE games SET time = ?, victory = ?, allGames = ? WHERE username = ?",
            updatedGame.time, updatedGame.victory, updatedGame.allGames, username
        )
        // Повертаємо оновлений об'єкт гри
        return ResponseEntity.ok(updatedGame.copy(username = username))
    }

    @GetMapping("/{username}")
    fun getGameByUsername(@PathVariable username: String): ResponseEntity<Game> {
        val game = findGameByUsername(username)
        return if (game != null) ResponseEntity.ok(game) else ResponseEntity.notFound().build()
    }

    private fun findGameByUsername(username: String): Game? {
        return jdbcTemplate.query(
            "SELECT * FROM games WHERE username = ?",
            arrayOf(username),
            rowMapper
        ).firstOrNull()
    }

    private val rowMapper = RowMapper<Game> { rs, _ ->
        Game(
            username = rs.getString("username"),
            time = rs.getLong("time").toInt(),
            victory = rs.getInt("victory"),
            allGames = rs.getInt("allGames")
        )
    }
}



