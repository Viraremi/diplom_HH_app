package ru.practicum.android.diploma.domain.api

import ru.practicum.android.diploma.domain.models.FilterOptions

interface FilterPreferences {
    fun saveFilters(options: FilterOptions)
    fun loadFilters(): FilterOptions?
    fun clearFilters()
}
