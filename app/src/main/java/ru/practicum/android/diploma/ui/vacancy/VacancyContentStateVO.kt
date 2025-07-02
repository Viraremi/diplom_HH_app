package ru.practicum.android.diploma.ui.vacancy

sealed interface VacancyContentStateVO {
    data object Base : VacancyContentStateVO
    data object Loading : VacancyContentStateVO
    data class Error(val code: Int) : VacancyContentStateVO
    data class SetFavorite(val isFavorite: Boolean) : VacancyContentStateVO
    data class Success(val vacancy: VacancyDetailsVO) : VacancyContentStateVO
}
