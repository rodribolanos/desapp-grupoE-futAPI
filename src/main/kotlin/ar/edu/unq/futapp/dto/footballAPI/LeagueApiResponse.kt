package ar.edu.unq.futapp.dto.footballAPI

data class LeagueApiResponse(
    val response: List<LeagueResponseItem>,
    val results: Int
)

data class LeagueResponseItem(
    val league: LeagueInfo
)

data class LeagueInfo(
    val id: Int,
    val name: String,
    val type: String,
    val logo: String
)