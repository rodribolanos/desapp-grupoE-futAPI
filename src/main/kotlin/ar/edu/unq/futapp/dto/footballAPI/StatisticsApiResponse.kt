package ar.edu.unq.futapp.dto.footballAPI

data class StatisticsApiResponse(
    val response: TeamStatisticsResponse,
    val results: Int
)

data class TeamStatisticsResponse(
    val team: BasicTeamInfo,
    val lineups: List<LineupInfo>,
    val goals: GoalsStatistics,
    val cards: CardsStatistics
)

data class BasicTeamInfo(
    val id: Int,
    val name: String
)

data class LineupInfo(
    val formation: String,
    val played: Int
)

data class GoalsStatistics(
    val `for`: GoalDirection,
    val against: GoalDirection
)

data class GoalDirection(
    val minute: Map<String, PeriodStatistic>
)

data class CardsStatistics(
    val yellow: Map<String, PeriodStatistic>
)

data class PeriodStatistic(
    val total: Int?,
    val percentage: String?
)

