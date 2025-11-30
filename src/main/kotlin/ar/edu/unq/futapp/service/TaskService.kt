package ar.edu.unq.futapp.service

import ar.edu.unq.futapp.model.ProcessStatus

interface TaskService {
    fun startTeamComparisonTask(team1: String, team2: String): ProcessStatus
    fun performComparisonTask(taskId: String, team1: String, team2: String): Unit
    fun getProcessStatusById(taskId: String): ProcessStatus
}