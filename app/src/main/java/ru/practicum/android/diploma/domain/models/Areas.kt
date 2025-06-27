package ru.practicum.android.diploma.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Areas(
    val id: String,
    val name: String,
    val parentId: String?,
    val areas: List<Areas>
)
