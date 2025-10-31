package ar.edu.unq.futapp.utils

import ar.edu.unq.futapp.model.Player
import ar.edu.unq.futapp.model.Team
import java.io.File
import java.net.URI

object TeamApiUtils {
    private const val STATIC_PATH = "src/test/resources/static"

    // URI helpers
    fun playerListPageUri(): URI = File("$STATIC_PATH/player-list-page.html").toURI()
    fun malformedPlayerListPageUri(): URI = File("$STATIC_PATH/malformed-player-list-page.html").toURI()
    fun searchTeamWithResults(): URI = File("$STATIC_PATH/search-team-with-results.html").toURI()
    fun pageWithoutResultsUri(): URI = File("$STATIC_PATH/page-without-results.html").toURI()
    fun playerSearchPageWithResults(): URI = File("$STATIC_PATH/search-player-with-results.html").toURI()
    fun playerSearchPageWithoutResults(): URI = File("$STATIC_PATH/search-player-without-results.html").toURI()
    // NUEVO: fixtures
    fun teamFixturesPageUri(): URI = File("$STATIC_PATH/team-fixtures-page.html").toURI()
    fun malformedTeamFixturesPageUri(): URI = File("$STATIC_PATH/malformed-team-fixtures-page.html").toURI()


    // Expected data for player-list-page.html
    fun expectedPlayersForPlayerListPage(): List<Player> = listOf(
        Player("Rodrigo Battaglia", 3, 1, 0, 7.21),
        Player("Lautaro Blanco", 3, 0, 0, 6.99),
        Player("Miguel Merentiel", 3, 1, 0, 6.96),
        Player("Kevin Zenón", 2, 0, 0, 6.90),
        Player("Alan Velasco", 3, 0, 1, 6.81),
        Player("Ayrton Costa", 2, 0, 1, 6.80),
        Player("Edinson Cavani", 1, 0, 0, 6.77),
        Player("Carlos Palacios", 3, 0, 0, 6.67),
        Player("Marco Pellegrino", 1, 0, 0, 6.64),
        Player("Tomás Belmonte", 1, 0, 0, 6.61),
        Player("Luis Advíncula", 3, 0, 0, 6.52),
        Player("Lautaro Di Lollo", 2, 0, 0, 6.50),
        Player("Agustín Marchesín", 3, 0, 0, 6.48),
        Player("Malcom Braida", 0, 0, 0, 6.42),
        Player("Exequiel Zeballos", 1, 0, 0, 6.17),
        Player("Ander Herrera", 1, 0, 0, 6.12),
        Player("Williams Alarcón", 0, 0, 0, 6.08),
        Player("Marcelo Saracchi", 0, 0, 0, 6.03),
        Player("Milton Giménez", 0, 0, 0, 6.01),
        Player("Marcos Rojo", 1, 0, 0, 6.00),
        Player("Nicolás Figal", 1, 0, 0, 5.83)
    )

    fun expectedTeamForPlayerListPage(): Team = Team(
        name = "Boca Juniors",
        players = expectedPlayersForPlayerListPage().toMutableList()
    )
}