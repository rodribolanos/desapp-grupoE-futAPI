package ar.edu.unq.futapp.model

import java.io.Serializable

data class RequestAuditId(
    var userId: String = "",
    var seqId: Long? = null
) : Serializable {
    constructor() : this("", null)
}
