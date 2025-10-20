package ar.edu.unq.futapp.dto

import org.springframework.data.domain.Page

data class PageResponseDTO<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int
)

fun <T, R> Page<T>.toPageDTO(mapper: (T) -> R): PageResponseDTO<R> = PageResponseDTO(
    content = this.content.map(mapper),
    page = this.number,
    size = this.size,
    totalElements = this.totalElements,
    totalPages = this.totalPages
)
