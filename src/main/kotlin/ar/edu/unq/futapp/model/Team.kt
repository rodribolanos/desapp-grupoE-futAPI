package ar.edu.unq.futapp.model

import jakarta.persistence.*

@Entity
class Team() {
    @Id
    var name: String = ""

    @OneToMany(mappedBy = "team", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    var players: MutableList<Player> = mutableListOf()

    constructor(name: String, players: MutableList<Player>) : this() {
        this.name = name
        this.players = players
        players.forEach { it.team = this }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as Team
        if (name.isBlank() || other.name.isBlank()) return false
        return name == other.name && players == other.players
    }

    override fun hashCode(): Int = if (name.isBlank()) javaClass.hashCode() else name.hashCode()
}