package ar.edu.unq.futapp.model

import jakarta.persistence.*

@Entity
class Player() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var name: String = ""
    var playedGames: Int = 0
    var goals: Int = 0
    var assists: Int = 0
    var rating: Double = 0.0

    @ManyToOne
    var team: Team? = null

    constructor(name: String, playedGames: Int, goals: Int, assists: Int, rating: Double) : this() {
        this.name = name
        this.playedGames = playedGames
        this.goals = goals
        this.assists = assists
        this.rating = rating
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Player) return false
        return id == other.id &&
                name == other.name &&
                playedGames == other.playedGames &&
                goals == other.goals &&
                assists == other.assists &&
                rating == other.rating &&
                team?.name == other.team?.name
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
