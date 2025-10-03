package ar.edu.unq.futapp.service

import ar.edu.unq.futapp.model.PlayerPerformance

interface PlayerService {
    fun findPlayerPerformanceByName(playerName: String): PlayerPerformance
}