package ar.edu.unq.futapp.model

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity
class Team() {
    @Id
    lateinit var name: String
    @OneToMany(mappedBy = "team", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    lateinit var players: MutableList<Player>

    constructor(name: String, players: MutableList<Player>) : this() {
        this.name = name
        this.players = players
        players.forEach { it.team = this }
    }
}