package ar.edu.unq.futapp.builders

import ar.edu.unq.futapp.model.Performance
import ar.edu.unq.futapp.model.PlayerPerformance

class PlayerPerformanceBuilder {
    private var name: String = "Default Player"
    private var seasons: MutableList<Performance> = mutableListOf()

    fun withName(value: String) = apply { this.name = value }
    fun withSeasons(seasons: List<Performance>) = apply { this.seasons = seasons.toMutableList() }
    fun addSeason(performance: Performance) = apply { this.seasons.add(performance) }

    fun build() = PlayerPerformance(name, seasons)
}