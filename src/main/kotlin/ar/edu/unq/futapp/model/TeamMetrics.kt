package ar.edu.unq.futapp.model

class TeamMetrics(
    val teamName: String,
    val goalsAverage: Double,
    val fairPlay: FairPlay,
    val possessionPercentage: Double,
    val passAccuracy: Double,
    val shotsPerGame: Double,
    val aerialDuelsWonPercentage: Double,

) {
    fun compareTo(other: TeamMetrics): Comparison {
        val goalsAverageComparison = this.compareGoalsAverage(other)
        val shotsPerGameComparison = this.compareShotsPerGame(other)
        val fairPlayComparison = this.compareFairPlay(other)
        val possessionComparison = this.comparePossessionPercentage(other)
        val passAccuracyComparison = this.comparePassAccuracy(other)
        val aerialDuelsWonComparison = this.compareAerialDuelsWonPercentage(other)

        return Comparison(
            goalsAverageComparison,
            shotsPerGameComparison,
            fairPlayComparison,
            possessionComparison,
            passAccuracyComparison,
            aerialDuelsWonComparison
        )
    }


    private fun determineWinner(val1: Double, val2: Double): Winner {
        val EPSILON = 0.05
        return when {
            Math.abs(val1 - val2) < Math.max(val1, val2) * EPSILON -> Winner.DRAW
            val1 > val2 -> Winner.TEAM1
            else -> Winner.TEAM2
        }
    }

    private fun determineWinnerFromMinValue(val1: Double, val2: Double): Winner {
        val EPSILON = 0.05
        return when {
            Math.abs(val1 - val2) < EPSILON -> Winner.DRAW
            val1 < val2 -> Winner.TEAM1
            else -> Winner.TEAM2
        }
    }

    private fun compareGoalsAverage(other: TeamMetrics): ComparisonResult {
        return ComparisonResult(
            this.goalsAverage,
            other.goalsAverage,
            determineWinner(this.goalsAverage, other.goalsAverage)
        )
    }

    private fun compareShotsPerGame(other: TeamMetrics): ComparisonResult {
        return ComparisonResult(
            this.shotsPerGame,
            other.shotsPerGame,
            determineWinner(this.shotsPerGame, other.shotsPerGame)
        )
    }

    private fun comparePossessionPercentage(other: TeamMetrics): ComparisonResult {
        return ComparisonResult(
            this.possessionPercentage,
            other.possessionPercentage,
            determineWinner(this.possessionPercentage, other.possessionPercentage)
        )
    }

    private fun comparePassAccuracy(other: TeamMetrics): ComparisonResult {
        return ComparisonResult(
            this.passAccuracy,
            other.passAccuracy,
            determineWinner(this.passAccuracy, other.passAccuracy)
        )
    }

    private fun compareAerialDuelsWonPercentage(other: TeamMetrics): ComparisonResult {
        return ComparisonResult(
            this.aerialDuelsWonPercentage,
            other.aerialDuelsWonPercentage,
            determineWinner(this.aerialDuelsWonPercentage, other.aerialDuelsWonPercentage)
        )
    }


    private fun compareFairPlay(other: TeamMetrics): ComparisonResult {
        // Criteria: Less amount of points is the winner.
        // Points: RedCards = 3, YellowCard = 1.

        val team1FairPlayScore = (this.fairPlay.redCards * 3) + this.fairPlay.yellowCards
        val team2FairPlayScore = (other.fairPlay.redCards * 3) + other.fairPlay.yellowCards

        val winner = determineWinnerFromMinValue(team1FairPlayScore.toDouble(), team2FairPlayScore.toDouble())

        return ComparisonResult(
            team1FairPlayScore.toDouble(),
            team2FairPlayScore.toDouble(),
            winner
        )
    }
}