package ar.edu.unq.futapp.service.impl

import ar.edu.unq.futapp.model.PlayerPerformance
import ar.edu.unq.futapp.service.PlayerService
import ar.edu.unq.futapp.service.WhoScoredApiClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PlayerServiceImpl @Autowired constructor(val whoScoredApiClient: WhoScoredApiClient) : PlayerService {
    override fun findPlayerPerformanceByName(playerName: String): PlayerPerformance {
        val playerData = whoScoredApiClient.findPlayerPerformance(playerName)
        return playerData.get()
    }

}