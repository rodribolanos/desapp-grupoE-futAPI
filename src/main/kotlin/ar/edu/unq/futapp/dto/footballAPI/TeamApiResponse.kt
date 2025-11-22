package ar.edu.unq.futapp.dto.footballAPI

data class TeamApiResponse(
    val response: List<TeamResponseItem>,
    val results: Int
)

data class TeamResponseItem(
    val team: TeamInfo
)

data class TeamInfo(
    val id: Int,
    val name: String,
    val code: String?,
    val country: String?
)