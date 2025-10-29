package ar.edu.unq.futapp.repository

import ar.edu.unq.futapp.model.Team
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TeamRepository: JpaRepository<Team, String>
