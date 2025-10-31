package ar.edu.unq.futapp.model

data class Performance(
    val season: String,
    val competition: String,
    val team: String,
    val appearances: Int,
    val goals: Int,
    val assists: Int,
    val aerialWons: Double,
    val rating: Double
)