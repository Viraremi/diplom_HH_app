package ru.practicum.android.diploma.ui.filter.place.models

sealed interface CountryState {
    data object Loading : CountryState
    data object Empty : CountryState

    data class Error(
        val error: Int
    ) : CountryState

    data class Content(
        val countries: List<Country>
    ) : CountryState
}
