package ar.edu.unq.futapp.repository

import ar.edu.unq.futapp.model.ProcessStatus
import org.springframework.data.jpa.repository.JpaRepository

interface ProcessStatusRepository: JpaRepository<ProcessStatus, String> {
}