package ar.edu.unq.futapp.dto

import ar.edu.unq.futapp.model.Player

data class PlayerDTO(val name: String, val playedGames: Int, val goals: Int, val assists: Int, val rating : Double)

fun Player.toDTO(): PlayerDTO {
    return PlayerDTO(
        name = this.name,
        playedGames = this.playedGames,
        goals = this.goals,
        assists = this.assists,
        rating = this.rating
    )
}

fun List<Player>.toDTO(): List<PlayerDTO> {
    return this.map { it.toDTO() }
}