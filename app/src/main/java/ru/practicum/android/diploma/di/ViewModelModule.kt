package ru.practicum.android.diploma.di

import com.google.gson.Gson
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.ui.favorite.viewmodel.FavoriteViewModel
import ru.practicum.android.diploma.ui.filter.FilterViewModel
import ru.practicum.android.diploma.ui.filter.industry.IndustryViewModel
import ru.practicum.android.diploma.ui.filter.place.PlaceViewModel
import ru.practicum.android.diploma.ui.filter.place.country.CountryViewModel
import ru.practicum.android.diploma.ui.filter.place.models.Country
import ru.practicum.android.diploma.ui.filter.place.models.Region
import ru.practicum.android.diploma.ui.filter.place.region.RegionViewModel
import ru.practicum.android.diploma.ui.main.MainViewModel
import ru.practicum.android.diploma.ui.vacancy.VacancyViewModel

val viewModelModule = module {
    viewModel {
        MainViewModel(get(), get())
    }

    viewModel {
        VacancyViewModel(get(), get(), get())
    }

    viewModel {
        FavoriteViewModel(get())
    }

    viewModel {
        FilterViewModel(get(), get())
    }

    viewModel {
        IndustryViewModel(get())
    }

    viewModel {
        CountryViewModel(get(), get())
    }

    viewModel { (country: Country?) ->
        RegionViewModel(get(),
            get<Gson>().toJson(country),
            get()
        )
    }

    viewModel { (country: Country?, region: Region?) ->
        PlaceViewModel(
            get<Gson>().toJson(country),
            get<Gson>().toJson(region),
            get()
        )
    }
}
