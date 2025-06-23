package ru.practicum.android.diploma.data.filters

import android.content.SharedPreferences
import com.google.gson.Gson
import ru.practicum.android.diploma.domain.models.FilterOptions
import ru.practicum.android.diploma.util.FILTER_SETTINGS
import androidx.core.content.edit
import ru.practicum.android.diploma.domain.api.FilterPreferences

class FilterPreferencesImpl (private val prefs: SharedPreferences) : FilterPreferences {
    override fun saveFilters(options: FilterOptions) {
        val json = Gson().toJson(options)
        prefs.edit { putString(FILTER_SETTINGS, json) }
    }

    override fun loadFilters(): FilterOptions? {
        val json = prefs.getString(FILTER_SETTINGS, null)
        return Gson().fromJson(json, FilterOptions::class.java)
    }

    override fun clearFilters() {
        prefs.edit { remove(FILTER_SETTINGS) }
    }
}
