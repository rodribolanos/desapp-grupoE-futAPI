package ar.edu.unq.futapp.model

data class TeamComparisonResult(
    val team1: String,
    val team2: String,
    val comparisonData: Comparison
)

data class Comparison(
    val goalsAverage: ComparisonResult,
    val shootsAverage: ComparisonResult,
    val fairPlay: ComparisonResult,
    val posession: ComparisonResult,
    val passAccuracy: ComparisonResult,
    val aerialsWinsAverage: ComparisonResult
)

data class ComparisonResult(
    val team1Value: Double,
    val team2Value: Double,
    val betterTeam: Winner
)

enum class Winner{
    TEAM1,
    TEAM2,
    DRAW
}
