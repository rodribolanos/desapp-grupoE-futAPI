package ar.edu.unq.futapp.model

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "request_audit")
class RequestAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private var id: Long? = null

    @Column(name = "user_id", nullable = false, length = 100)
    private lateinit var userId: String

    @Column(nullable = false, length = 255)
    private lateinit var endpoint: String

    @Column(nullable = false)
    private var status: Int = 0

    @Column(name = "duration_ms", nullable = false)
    private var durationMs: Long = 0

    @Column(name = "created_at", nullable = false)
    private var createdAt: Instant = Instant.now()

    constructor()

    constructor(userId: String, endpoint: String, status: Int, durationMs: Long) {
        this.userId = userId
        this.endpoint = endpoint
        this.status = status
        this.durationMs = durationMs
        this.createdAt = Instant.now()
    }

    fun getId(): Long? = id
    fun getUserId(): String = userId
    fun getEndpoint(): String = endpoint
    fun getStatus(): Int = status
    fun getDurationMs(): Long = durationMs
    fun getCreatedAt(): Instant = createdAt
}
