package ar.edu.unq.futapp.unit.service.impl

import ar.edu.unq.futapp.exception.EntityNotFound
import ar.edu.unq.futapp.model.ProcessStatus
import ar.edu.unq.futapp.model.Status
import ar.edu.unq.futapp.model.TeamComparisonResult
import ar.edu.unq.futapp.repository.ProcessStatusRepository
import ar.edu.unq.futapp.service.TaskService
import ar.edu.unq.futapp.service.TeamService
import ar.edu.unq.futapp.service.impl.TaskServiceImpl
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*
import org.springframework.beans.factory.ObjectProvider
import java.util.Optional
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TaskServiceImplTest {

    // Mocks de dependencias
    @Mock private lateinit var processRepository: ProcessStatusRepository
    @Mock private lateinit var objectMapper: ObjectMapper
    @Mock private lateinit var teamService: TeamService
    @Mock private lateinit var selfProxyProvider: ObjectProvider<TaskService>

    // Objeto bajo prueba
    private lateinit var service: TaskService

    // Mock del servicio proxy para verificar llamadas asíncronas/transaccionales
    @Mock private lateinit var selfProxy: TaskService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        whenever(selfProxyProvider.getObject()).thenReturn(selfProxy)

        service = TaskServiceImpl(processRepository, objectMapper, teamService, selfProxyProvider)
    }

    @Test
    @DisplayName("performComparisonTask: should call handleComparisonFailure on exception")
    fun performComparisonTask_Failure() {
        // GIVEN
        val taskId = "123-failed"
        val team1 = "Equipo A"
        val team2 = "Equipo B"
        val initialStatus = ProcessStatus(id = taskId, status = Status.IN_PROCESS)
        val expectedException = RuntimeException("API Connection Error")

        whenever(processRepository.findById(taskId)).thenReturn(Optional.of(initialStatus))
        whenever(teamService.compareTeams(team1, team2)).thenThrow(expectedException)

        service.performComparisonTask(taskId, team1, team2)

        verify(selfProxy).handleComparisonFailure(
            eq(initialStatus),
            eq(expectedException)
        )
        // Verificar que el repositorio NO se llamó directamente con Status.FINISHED (solo lo debe hacer handleComparisonFailure)
        verify(processRepository, never()).save(any<ProcessStatus>())
    }

    @Test
    @DisplayName("handleComparisonFailure: should set status to FAILED and save error message")
    fun handleComparisonFailure_UpdatesStatus() {
        val currentStatus = ProcessStatus(id = "task-id", status = Status.IN_PROCESS)
        val errorMessage = "Bad Request from API"
        val exception = EntityNotFound(errorMessage)

        service.handleComparisonFailure(currentStatus, exception)

        val statusCaptor = ArgumentCaptor.forClass(ProcessStatus::class.java)
        verify(processRepository).save(statusCaptor.capture())

        assertEquals(Status.FAILED, statusCaptor.value.status)
        assertEquals(errorMessage, statusCaptor.value.errorMessage)
    }

    @Test
    @DisplayName("getProcessStatusById: should throw EntityNotFound if process ID is not found")
    fun getProcessStatusById_NotFound() {
        // GIVEN
        val nonExistentId = "999"
        // Mockear que findById no encuentra nada
        whenever(processRepository.findById(nonExistentId)).thenReturn(Optional.empty())

        // WHEN / THEN
        val exception = assertThrows<EntityNotFound> {
            service.getProcessStatusById(nonExistentId)
        }
        assertEquals("No process found with id 999", exception.message)
    }

    @Test
    @DisplayName("handleComparisonFailure: should use 'Unknown error' for null exception message")
    fun handleComparisonFailure_NullMessage() {
        val currentStatus = ProcessStatus(id = "task-id", status = Status.IN_PROCESS)
        val exception = Exception(null as String?)

        service.handleComparisonFailure(currentStatus, exception)

        val statusCaptor = ArgumentCaptor.forClass(ProcessStatus::class.java)
        verify(processRepository).save(statusCaptor.capture())

        assertEquals(Status.FAILED, statusCaptor.value.status)
        assertEquals("Unknown error", statusCaptor.value.errorMessage)
    }
}