package ar.edu.unq.futapp.service.impl

import ar.edu.unq.futapp.events.UpdateTeamEvent
import ar.edu.unq.futapp.exception.EntityNotFound
import ar.edu.unq.futapp.model.AdvancedMetric
import ar.edu.unq.futapp.model.Player
import ar.edu.unq.futapp.model.Team
import ar.edu.unq.futapp.model.UpcomingMatch
import ar.edu.unq.futapp.repository.TeamRepository
import ar.edu.unq.futapp.service.FootballApiClient
import ar.edu.unq.futapp.service.WhoScoredApiClient
import ar.edu.unq.futapp.service.TeamService
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class TeamServiceImpl @Autowired constructor(
    val teamApiClient: WhoScoredApiClient,
    val teamRepository: TeamRepository,
    val eventPublisher: ApplicationEventPublisher,
    val footballApiClient: FootballApiClient
) : TeamService {
    @Transactional
    override fun findPlayersByTeam(teamName: String): List<Player> {
        var team: Optional<Team> = teamRepository.findById(teamName)

        if (team.isPresent) {
            eventPublisher.publishEvent(UpdateTeamEvent(teamName, this))
            return team.get().players
        }

        team = teamApiClient.findTeam(teamName)
        if (team.isEmpty) throw EntityNotFound("Team with name $teamName not found")
        teamRepository.save(team.get())
        return team.get().players
    }

    override fun findUpcomingFixturesByTeam(teamName: String): List<UpcomingMatch> {
        val matchesOpt = teamApiClient.findUpcomingFixtures(teamName)
        if (matchesOpt.isEmpty) throw EntityNotFound("Team with name $teamName not found")
        return matchesOpt.get()
    }

    override fun getAdvancedMetricsForTeamAndCountry(teamName: String, country: String): AdvancedMetric {
        val teamId = footballApiClient.getIdForTeam(teamName)
        val leagueId = footballApiClient.getIdForCountryLeague(country)
        return footballApiClient.getAdvancedMetricsForTeamAndCountry(teamId, leagueId)
    }
    
  override fun compareTeams(team1: String, team2: String): TeamComparisonResult {
        val team1Stats: TeamMetrics = teamApiClient.findTeamMetrics(team1)
        val team2Stats: TeamMetrics = teamApiClient.findTeamMetrics(team2)

        val comparison = TeamComparisonResult(
            team1 = team1Stats.teamName,
            team2 = team2Stats.teamName,
            comparisonData = team1Stats.compareTo(team2Stats)
        )
        return comparison
    }
}