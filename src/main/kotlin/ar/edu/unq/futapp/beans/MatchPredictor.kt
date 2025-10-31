package ar.edu.unq.futapp.beans

import ar.edu.unq.futapp.model.Match
import ar.edu.unq.futapp.model.MatchPrediction
import ar.edu.unq.futapp.model.PredictionWeights
import ar.edu.unq.futapp.model.StatisticalCalculator
import org.springframework.stereotype.Component

@Component
class MatchPredictor(
    val weights: PredictionWeights = PredictionWeights()
) {

    fun predictMatch(homeTeam:String, awayTeam:String, historicalMatches:List<Match>): MatchPrediction {
        println(historicalMatches)
        // Calculate components using StatisticalCalculator
        val homeWinRate = StatisticalCalculator.calculateWeightedWinPercentage(historicalMatches, homeTeam, true)
        val awayWinRate = StatisticalCalculator.calculateWeightedWinPercentage(historicalMatches, awayTeam, false)
        val homeAdvantage = StatisticalCalculator.calculateHomeAdvantage(homeTeam, historicalMatches)
        val homeForm = StatisticalCalculator.calculateRecentForm(historicalMatches, homeTeam)
        val awayForm = StatisticalCalculator.calculateRecentForm(historicalMatches, awayTeam)

        // Combine factors
        val homeScore = (
            homeWinRate * weights.historicalWeight +
                    homeAdvantage * weights.homeAdvantageWeight +
                    homeForm * weights.formWeight
            )

        val awayScore = (
            awayWinRate * weights.historicalWeight +
                    (1 - homeAdvantage) * weights.homeAdvantageWeight +
                    awayForm * weights.formWeight)

        // Normalize to probabilities
        val total = homeScore + awayScore + weights.drawBaseline

        return MatchPrediction(
            homeTeam = homeTeam,
            awayTeam = awayTeam,
            homeWinProbability = homeScore / total,
            drawProbability = weights.drawBaseline / total,
            awayWinProbability = awayScore / total
        )
    }

}
