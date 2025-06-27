package ru.practicum.android.diploma.ui.filter.place

import com.google.gson.Gson
import ru.practicum.android.diploma.ui.filter.place.models.Country
import ru.practicum.android.diploma.ui.filter.place.models.Region

class AreasConverter(private val gson: Gson) {
    fun deSerializeRegion(region: Region): String {
        return gson.toJson(region)
    }

    fun deSerializeCountry(country: Country): String {
        return gson.toJson(country)
    }

    fun serializeRegion(regionJson: String): Region {
        return gson.fromJson(regionJson, Region::class.java)
    }

    fun serializeCountry(countryJson: String): Country {
        return gson.fromJson(countryJson, Country::class.java)
    }
}
