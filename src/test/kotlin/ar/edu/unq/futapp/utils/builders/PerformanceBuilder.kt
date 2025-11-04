package ar.edu.unq.futapp.utils.builders

import ar.edu.unq.futapp.model.Performance

class PerformanceBuilder {
    private var season: String = ""
    private var competition: String = ""
    private var team: String = ""
    private var appearances: Int = 0
    private var goals: Int = 0
    private var assists: Int = 0
    private var aerialWons: Double = 0.0
    private var rating: Double = 0.0

    fun withSeason(value: String) = apply { this.season = value }
    fun withCompetition(value: String) = apply { this.competition = value }
    fun withTeam(value: String) = apply { this.team = value }
    fun withAppearances(value: Int) = apply { this.appearances = value }
    fun withGoals(value: Int) = apply { this.goals = value }
    fun withAssists(value: Int) = apply { this.assists = value }
    fun withAerialWons(value: Double) = apply { this.aerialWons = value }
    fun withRating(value: Double) = apply { this.rating = value }

    fun build() = Performance(season, competition, team, appearances, goals, assists, aerialWons, rating)

}
