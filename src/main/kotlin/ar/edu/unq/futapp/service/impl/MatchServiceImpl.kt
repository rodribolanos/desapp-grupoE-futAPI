package ar.edu.unq.futapp.service.impl

import ar.edu.unq.futapp.beans.MatchPredictor
import ar.edu.unq.futapp.model.MatchPrediction
import ar.edu.unq.futapp.service.MatchService
import ar.edu.unq.futapp.service.WhoScoredApiClient
import org.springframework.stereotype.Service

@Service
class MatchServiceImpl(
    private val matchPredictor: MatchPredictor,
    private val whoScoredApiClient: WhoScoredApiClient
)
    : MatchService {

    override fun predictMatch(homeTeam: String, awayTeam: String): MatchPrediction {

        var homeTeamLastMatches = whoScoredApiClient.findLastMatchesFromTeam(homeTeam)
        var awayTeamLastMatches = whoScoredApiClient.findLastMatchesFromTeam(awayTeam)

        val historicalMatches = homeTeamLastMatches + awayTeamLastMatches
        return matchPredictor.predictMatch(homeTeam, awayTeam, historicalMatches)
    }
}