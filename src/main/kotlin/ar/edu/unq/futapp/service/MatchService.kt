package ar.edu.unq.futapp.service

import ar.edu.unq.futapp.model.MatchPrediction

interface MatchService {
    fun predictMatch(homeTeam: String, awayTeam: String): MatchPrediction
}