package ar.edu.unq.futapp.repository

import ar.edu.unq.futapp.model.ProcessStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.Instant

interface ProcessStatusRepository: JpaRepository<ProcessStatus, String> {
    @Modifying
    @Query("DELETE FROM ProcessStatus p WHERE (p.status = 'FINISHED' OR p.status = 'FAILED') AND p.createdAt < :cutoffTime")
    fun deleteOldFinishedOrFailedProcesses(cutoffTime: Instant): Int
}