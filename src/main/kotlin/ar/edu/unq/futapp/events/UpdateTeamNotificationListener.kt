package ar.edu.unq.futapp.events

import ar.edu.unq.futapp.repository.TeamRepository
import ar.edu.unq.futapp.service.WhoScoredApiClient
import ar.edu.unq.futapp.service.impl.TeamServiceImpl
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class UpdateTeamNotificationListener(val teamApiClient: WhoScoredApiClient, val teamRepository: TeamRepository) {
    private val pending = ConcurrentHashMap.newKeySet<String>()
    private val logger = LoggerFactory.getLogger(TeamServiceImpl::class.java)

    @EventListener
    @Async
    fun handleUpdateTeamEvent(event: UpdateTeamEvent) {
        val teamName = event.name
        try {
            if(!tryRegister(teamName)) return
            val teamOpt = teamApiClient.findTeam(teamName)
            if (teamOpt.isPresent) {
                teamRepository.save(teamOpt.get())
                logger.info("Team $teamName updated")
            }
        } finally {
            unregister(teamName)
        }
    }

    fun tryRegister(teamName: String): Boolean = pending.add(teamName)

    fun unregister(teamName: String) {
        pending.remove(teamName)
    }
}