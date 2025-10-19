package ar.edu.unq.futapp.model

data class PredictionWeights(
    val historicalWeight: Double = 0.4,
    val homeAdvantageWeight: Double = 0.2,
    val formWeight: Double = 0.3,
    val drawBaseline: Double = 0.1
)