package ar.edu.unq.futapp.utils.builders

import ar.edu.unq.futapp.model.FairPlay

class FairPlayBuilder {
    var redCards: Int = 0
    var yellowCards: Int = 0

    fun withRedCards(redCards: Int) = apply { this.redCards = redCards }
    fun withYellowCards(yellowCards: Int) = apply { this.yellowCards = yellowCards }

    fun build() = FairPlay(
        redCards = redCards,
        yellowCards = yellowCards
    )
}