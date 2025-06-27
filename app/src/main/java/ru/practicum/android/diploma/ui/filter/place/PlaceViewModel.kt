package ru.practicum.android.diploma.ui.filter.place

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import ru.practicum.android.diploma.ui.filter.place.models.Country
import ru.practicum.android.diploma.ui.filter.place.models.PlaceState
import ru.practicum.android.diploma.ui.filter.place.models.Region

class PlaceViewModel(
    country: String?,
    region: String?,
    private val gson: Gson
) : ViewModel() {
    private var countryLocal: Country? = null
    private var regionLocal: Region? = null

    private val stateLiveData = MutableLiveData<PlaceState>()
    fun observeState(): LiveData<PlaceState> = stateLiveData

    init {
        countryLocal = gson.fromJson(country, Country::class.java)
        regionLocal = gson.fromJson(region, Region::class.java)
        stateLiveData.postValue(PlaceState.Content(countryLocal, regionLocal))
    }

    fun changeCountry(countryChange: String?) {
        countryLocal = gson.fromJson(countryChange, Country::class.java)
        regionLocal = null
        stateLiveData.postValue(PlaceState.Content(countryLocal, regionLocal))
    }

    fun changeRegion(regionChange: String?) {
        regionLocal = gson.fromJson(regionChange, Region::class.java)
        stateLiveData.postValue(PlaceState.Content(countryLocal, regionLocal))
    }

    fun responseRegion() {
        stateLiveData.postValue(PlaceState.ResponseRegion(countryLocal))
    }

    fun saveChanged() {
        stateLiveData.postValue(PlaceState.Save(countryLocal, regionLocal))
    }

    fun clearLiveData() {
        stateLiveData.postValue(PlaceState.Loading)
    }
}
