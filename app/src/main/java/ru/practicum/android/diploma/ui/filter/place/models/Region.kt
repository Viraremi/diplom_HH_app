package ru.practicum.android.diploma.ui.filter.place.models

import kotlinx.serialization.Serializable

@Serializable
data class Region(
    val id: String,
    val name: String,
    val country: Country?,
)
