package ar.edu.unq.futapp.model

import jakarta.persistence.*

@Entity
class Player() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
    var name: String = ""
    var playedGames: Int = 0
    var goals: Int = 0
    var assists: Int = 0
    var rating : Double = 0.0
    @ManyToOne
    var team: Team? = null

    constructor(name: String, playedGames: Int, goals: Int, assists: Int, rating: Double) : this() {
        this.name = name
        this.playedGames = playedGames
        this.goals = goals
        this.assists = assists
        this.rating = rating
    }
}