package ar.edu.unq.futapp.unit.events

import ar.edu.unq.futapp.events.UpdateTeamEvent
import ar.edu.unq.futapp.events.UpdateTeamNotificationListener
import ar.edu.unq.futapp.model.Team
import ar.edu.unq.futapp.repository.TeamRepository
import ar.edu.unq.futapp.service.WhoScoredApiClient
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles
import java.util.*
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

@ActiveProfiles("test")
class UpdateTeamNotificationListenerTest {

    private val apiClient: WhoScoredApiClient = mockk()
    private val repository: TeamRepository = mockk()
    private val listener: UpdateTeamNotificationListener = UpdateTeamNotificationListener(apiClient, repository)

    @Test
    @DisplayName("when team is found in api then it is saved")
    fun whenTeamFound_thenSaves() {
        // Arrange
        val teamName = "River"
        val team = Team(teamName, mutableListOf())
        every { apiClient.findTeam(teamName) } returns Optional.of(team)
        every { repository.save(team) } returns team

        // Act
        listener.handleUpdateTeamEvent(UpdateTeamEvent(teamName, this))

        // Assert
        verify(exactly = 1) { apiClient.findTeam(teamName) }
        verify(exactly = 1) { repository.save(team) }
        confirmVerified(apiClient, repository)
    }

    @Test
    @DisplayName("when team is not found in api then it is not saved")
    fun whenTeamNotFound_thenDoesNotSave() {
        // Arrange
        val teamName = "Boca"
        every { apiClient.findTeam(teamName) } returns Optional.empty()

        // Act
        listener.handleUpdateTeamEvent(UpdateTeamEvent(teamName, this))

        // Assert
        verify(exactly = 1) { apiClient.findTeam(teamName) }
        verify(exactly = 0) { repository.save(any()) }
        confirmVerified(apiClient, repository)
    }

    @Test
    @DisplayName("concurrent events: first thread blocks in client, barrier releases after 4 others finish; only one effective call")
    fun whenConcurrentEvents_firstBlocks_barrierAfter4_finish_thenOnlyOneEffectiveCall() {
        // Arrange
        val teamName = "Lanus"
        val team = Team(teamName, mutableListOf())
        val parties = 5 // 1 (primer hilo dentro de findTeam) + 4 hilos secundarios al terminar
        val start = CyclicBarrier(parties)
        val done = CyclicBarrier(parties)
        val firstCall = AtomicBoolean(true)
        val firstThread = AtomicReference<Thread>()

        every { apiClient.findTeam(teamName) } answers {
            if (firstCall.compareAndSet(true, false)) {
                firstThread.set(Thread.currentThread())
                // Primera llamada: se bloquea hasta que los otros 4 hilos terminen
                done.await(3, TimeUnit.SECONDS)
                Optional.of(team)
            } else {
                // No debería ser llamada si el lock funciona; si lo hace, igual respondemos
                Optional.of(team)
            }
        }
        every { repository.save(team) } returns team

        val pool = Executors.newFixedThreadPool(parties)
        val event = UpdateTeamEvent(teamName, this)

        // Act: arrancar todos a la vez, solo el primer hilo debe llegar al cliente
        repeat(parties) {
            pool.submit {
                start.await(3, TimeUnit.SECONDS)
                listener.handleUpdateTeamEvent(event)
                // Los hilos secundarios señalan finalización en la barrera 'done'
                if (Thread.currentThread() != firstThread.get()) {
                    done.await(3, TimeUnit.SECONDS)
                }
            }
        }
        pool.shutdown()
        pool.awaitTermination(5, TimeUnit.SECONDS)

        // Assert: sólo una llamada efectiva a cliente y un save
        verify(exactly = 1) { apiClient.findTeam(teamName) }
        verify(exactly = 1) { repository.save(team) }
        confirmVerified(apiClient, repository)
    }
}
