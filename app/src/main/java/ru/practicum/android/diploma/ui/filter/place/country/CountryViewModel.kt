package ru.practicum.android.diploma.ui.filter.place.country

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.data.network.NoInternetException
import ru.practicum.android.diploma.domain.filters.AreasInteractor
import ru.practicum.android.diploma.domain.models.Areas
import ru.practicum.android.diploma.ui.filter.place.models.Country
import ru.practicum.android.diploma.ui.filter.place.models.CountryState
import ru.practicum.android.diploma.util.HTTP_500_INTERNAL_SERVER_ERROR
import ru.practicum.android.diploma.util.HTTP_NO_CONNECTION

class CountryViewModel(
    areasInteractor: AreasInteractor,
    private val gson: Gson
) : ViewModel() {
    private val countryFilterState = MutableLiveData<CountryState>()
    val observeState: LiveData<CountryState> = countryFilterState

    private val countryList: ArrayList<Country> = arrayListOf()

    init {
        render(CountryState.Loading)
        viewModelScope.launch {
            areasInteractor.getAreas().collect { pair ->
                processAreas(pair.first, pair.second)
            }
        }
    }

    private fun processAreas(areas: List<Areas>?, error: Throwable?) {
        if (areas != null) {
            countryList.clear()
            fillCountryList(areas)
            render(CountryState.Content(countryList.toList()))
        }
        if (error != null) {
            if (error is NoInternetException) {
                render(CountryState.Error(HTTP_NO_CONNECTION))
            } else {
                render(CountryState.Error(HTTP_500_INTERNAL_SERVER_ERROR))
            }
        }
    }

    private fun fillCountryList(areas: List<Areas>) {
        for (country in areas) {
            if (country.parentId == null) {
                countryList.add(
                    Country(
                        id = country.id,
                        name = country.name
                    )
                )
            }
        }
    }

    private fun render(state: CountryState) {
        countryFilterState.postValue(state)
    }

    fun serializeCountry(country: Country): String {
        return gson.toJson(country)
    }
}
