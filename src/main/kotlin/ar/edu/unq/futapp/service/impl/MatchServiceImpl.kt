package ar.edu.unq.futapp.service.impl

import ar.edu.unq.futapp.beans.MatchPredictor
import ar.edu.unq.futapp.model.MatchPrediction
import ar.edu.unq.futapp.model.TeamMatches
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

        var homeTeamMatches: TeamMatches = whoScoredApiClient.findLastMatchesFromTeam(homeTeam)
        println(homeTeamMatches)

        var awayTeamMatches: TeamMatches = whoScoredApiClient.findLastMatchesFromTeam(awayTeam)
        println(awayTeamMatches)
        val historicalMatches = homeTeamMatches.matches + awayTeamMatches.matches
        return matchPredictor.predictMatch(
            homeTeamMatches.teamName,
            awayTeamMatches.teamName,
            historicalMatches
        )
    }
}