package ru.practicum.android.diploma.data.network

class NoInternetException : Exception {
    constructor() : super("No Internet Connection")
}
