package ar.edu.unq.futapp.service

import ar.edu.unq.futapp.model.Player
import ar.edu.unq.futapp.model.TeamComparisonResult
import ar.edu.unq.futapp.model.UpcomingMatch

interface TeamService {
    fun findPlayersByTeam(teamName: String): List<Player>
    fun findUpcomingFixturesByTeam(teamName: String): List<UpcomingMatch>
    fun compareTeams(team1: String, team2: String): TeamComparisonResult?
}