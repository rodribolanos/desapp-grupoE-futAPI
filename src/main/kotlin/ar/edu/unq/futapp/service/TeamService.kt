package ar.edu.unq.futapp.service

import ar.edu.unq.futapp.model.Player

interface TeamService {
    fun findPlayersByTeam(teamName: String): List<Player>
}