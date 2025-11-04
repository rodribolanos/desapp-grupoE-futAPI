package ar.edu.unq.futapp.unit.service.impl

import ar.edu.unq.futapp.events.UpdateTeamEvent
import ar.edu.unq.futapp.exception.EntityNotFound
import ar.edu.unq.futapp.model.Player
import ar.edu.unq.futapp.repository.TeamRepository
import ar.edu.unq.futapp.service.WhoScoredApiClient
import ar.edu.unq.futapp.service.impl.TeamServiceImpl
import ar.edu.unq.futapp.utils.TeamApiUtils
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.verify
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher
import org.springframework.test.context.ActiveProfiles
import java.util.Optional

@ActiveProfiles("test")
class TeamServiceImplTest {

    private lateinit var teamApiClient: WhoScoredApiClient
    private lateinit var teamRepository: TeamRepository
    private lateinit var eventPublisher: ApplicationEventPublisher
    private lateinit var service: TeamServiceImpl

    @BeforeEach
    fun setUp() {
        teamApiClient = mockk()
        teamRepository = mockk()
        eventPublisher = mockk(relaxed = true)
        service = TeamServiceImpl(teamApiClient, teamRepository, eventPublisher)
    }

    @Test
    @DisplayName("when team exists in repository then returns its players and publishes update event")
    fun whenTeamExistsInRepository_thenReturnsPlayersAndPublishesEvent() {
        // Arrange
        val teamName = "River"
        val players = listOf(
            Player("P1", 0, 0, 0, 0.0),
            Player("P2", 0, 0, 0, 0.0)
        )
        val team = TeamApiUtils.teamWithPlayers(teamName, players)
        every { teamRepository.findById(teamName) } returns Optional.of(team)

        // Act
        val result = service.findPlayersByTeam(teamName)

        // Assert
        assertEquals(players, result)
        verify(exactly = 1) { teamRepository.findById(teamName) }
        verify(exactly = 0) { teamApiClient.findTeam(any()) }
        verify(exactly = 0) { teamRepository.save(any()) }
        val eventSlot = slot<UpdateTeamEvent>()
        verify(exactly = 1) { eventPublisher.publishEvent(capture(eventSlot)) }
        assertTrue(eventSlot.captured is UpdateTeamEvent)
        assertEquals(teamName, eventSlot.captured.name)
        confirmVerified(teamRepository, teamApiClient, eventPublisher)
    }

    @Test
    @DisplayName("when team not in repository then fetch from client, persist and return players without publishing event")
    fun whenTeamNotInRepository_thenFetchFromClientPersistAndReturnPlayers() {
        // Arrange
        val teamName = "Boca"
        val players = listOf(Player("P3", 0, 0, 0, 0.0))
        val clientTeam = TeamApiUtils.teamWithPlayers(teamName, players)
        every { teamRepository.findById(teamName) } returns Optional.empty()
        every { teamApiClient.findTeam(teamName) } returns Optional.of(clientTeam)
        every { teamRepository.save(clientTeam) } returns clientTeam

        // Act
        val result = service.findPlayersByTeam(teamName)

        // Assert
        assertEquals(players, result)
        verify(exactly = 1) { teamRepository.findById(teamName) }
        verify(exactly = 1) { teamApiClient.findTeam(teamName) }
        verify(exactly = 1) { teamRepository.save(clientTeam) }
        verify(exactly = 0) { eventPublisher.publishEvent(any()) }
        confirmVerified(teamRepository, teamApiClient, eventPublisher)
    }

    @Test
    @DisplayName("when team not found neither in repository nor client then throws EntityNotFound and does not persist nor publish event")
    fun whenTeamMissingEverywhere_thenThrowsEntityNotFound() {
        // Arrange
        val teamName = "Desconocido"
        every { teamRepository.findById(teamName) } returns Optional.empty()
        every { teamApiClient.findTeam(teamName) } returns Optional.empty()

        // Act + Assert
        assertThrows(EntityNotFound::class.java) {
            service.findPlayersByTeam(teamName)
        }
        verify(exactly = 1) { teamRepository.findById(teamName) }
        verify(exactly = 1) { teamApiClient.findTeam(teamName) }
        verify(exactly = 0) { teamRepository.save(any()) }
        verify(exactly = 0) { eventPublisher.publishEvent(any()) }
        confirmVerified(teamRepository, teamApiClient, eventPublisher)
    }

    @Test
    @DisplayName("when team exists in repository with no players then returns empty list and publishes update event")
    fun whenTeamExistsWithNoPlayers_thenReturnsEmptyAndPublishesEvent() {
        // Arrange
        val teamName = "Lanus"
        val team = TeamApiUtils.teamWithPlayers(teamName, emptyList())
        every { teamRepository.findById(teamName) } returns Optional.of(team)

        // Act
        val result = service.findPlayersByTeam(teamName)

        // Assert
        assertEquals(emptyList<Player>(), result)
        verify(exactly = 1) { teamRepository.findById(teamName) }
        verify(exactly = 0) { teamApiClient.findTeam(any()) }
        verify(exactly = 0) { teamRepository.save(any()) }
        val eventSlot = slot<UpdateTeamEvent>()
        verify(exactly = 1) { eventPublisher.publishEvent(capture(eventSlot)) }
        assertEquals(teamName, eventSlot.captured.name)
        confirmVerified(teamRepository, teamApiClient, eventPublisher)
    }

    @Test
    @DisplayName("when team not in repository and client returns team without players then persist and return empty list without publishing event")
    fun whenTeamNotInRepoAndClientReturnsNoPlayers_thenPersistAndReturnEmpty() {
        // Arrange
        val teamName = "Arsenal"
        val clientTeam = TeamApiUtils.teamWithPlayers(teamName, emptyList())
        every { teamRepository.findById(teamName) } returns Optional.empty()
        every { teamApiClient.findTeam(teamName) } returns Optional.of(clientTeam)
        every { teamRepository.save(clientTeam) } returns clientTeam

        // Act
        val result = service.findPlayersByTeam(teamName)

        // Assert
        assertEquals(emptyList<Player>(), result)
        verify(exactly = 1) { teamRepository.findById(teamName) }
        verify(exactly = 1) { teamApiClient.findTeam(teamName) }
        verify(exactly = 1) { teamRepository.save(clientTeam) }
        verify(exactly = 0) { eventPublisher.publishEvent(any()) }
        confirmVerified(teamRepository, teamApiClient, eventPublisher)
    }
}
