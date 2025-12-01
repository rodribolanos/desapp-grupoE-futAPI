package ar.edu.unq.futapp.service.impl

import ar.edu.unq.futapp.exception.EntityNotFound
import ar.edu.unq.futapp.model.ProcessStatus
import ar.edu.unq.futapp.model.Status
import ar.edu.unq.futapp.model.TeamComparisonResult
import ar.edu.unq.futapp.repository.ProcessStatusRepository
import ar.edu.unq.futapp.service.TaskService
import ar.edu.unq.futapp.service.TeamService
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.transaction.Transactional
import org.springframework.beans.factory.ObjectProvider
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.UUID

@Service
@Transactional
class TaskServiceImpl(
    private val processRepository: ProcessStatusRepository,
    private val objectMapper: ObjectMapper,
    private val teamService: TeamService,
    private val selfProxy: ObjectProvider<TaskService>
):TaskService {
    val RETENTION_MILLIS: Long = 12 * 60 * 60 * 1000 // 12 hours

    override fun startTeamComparisonTask(team1: String, team2: String): ProcessStatus {
        val taskId = UUID.randomUUID().toString()
        var initialStatus = ProcessStatus(id = taskId, status = Status.IN_PROCESS)

        initialStatus = processRepository.save(initialStatus)

        selfProxy.getObject().performComparisonTask(taskId, team1, team2)

        val responseStatus = initialStatus.copy(status = Status.NOT_STARTED)

        return responseStatus
    }

    @Async
    override fun performComparisonTask(taskId: String, team1: String, team2: String):   Unit {
        val currentStatus = this.getProcessStatusById(taskId)

        try {
            val comparison: TeamComparisonResult = teamService.compareTeams(team1, team2)
            val resultJson = objectMapper.writeValueAsString(comparison)

            currentStatus.resultJson = resultJson
            currentStatus.status = Status.FINISHED

            processRepository.save(currentStatus)
        } catch (e: Exception) {
            selfProxy.getObject().handleComparisonFailure(currentStatus, e)
        }
    }

    @Transactional
    override fun handleComparisonFailure(currentStatus: ProcessStatus, exception: Exception) {
        currentStatus.status = Status.FAILED
        currentStatus.errorMessage = exception.message ?: "Unknown error"
        processRepository.save(currentStatus)
    }

    override fun getProcessStatusById(taskId: String): ProcessStatus {
        return processRepository.findById(taskId).orElseThrow {
            EntityNotFound("No process found with id $taskId")
        }
    }

    @Scheduled(fixedRate = 3600000) // 3,600,000 ms = 1 hora
    @Transactional
    fun cleanupOldProcesses() {
        val cutoffTime = System.currentTimeMillis() - RETENTION_MILLIS
        val deletedCount = processRepository.deleteOldFinishedOrFailedProcesses(cutoffTime)
        println("Cleanup ran. Records deleted: $deletedCount (Cutoff: $cutoffTime)")

    }
}