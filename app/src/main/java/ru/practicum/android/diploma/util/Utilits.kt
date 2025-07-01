package ru.practicum.android.diploma.util

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.ui.filter.model.SelectedFilters
import java.io.Serializable

fun pxToDp(dp: Float, context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics
    ).toInt()
}

internal fun getCurrSymbol(context: Context, codeSymbol: String): String {
    return with(context) {
        when (codeSymbol.uppercase()) {
            "RUB", "RUR" -> getString(R.string.currency_rub)
            "BYR" -> getString(R.string.currency_byr)
            "USD" -> getString(R.string.currency_usd)
            "EUR" -> getString(R.string.currency_eur)
            "KZT" -> getString(R.string.currency_kzt)
            "UAH" -> getString(R.string.currency_uah)
            "AZN" -> getString(R.string.currency_azn)
            "UZS" -> getString(R.string.currency_uzs)
            "GEL" -> getString(R.string.currency_gel)
            "KGT" -> getString(R.string.currency_kgt)
            else -> codeSymbol
        }
    }
}

fun formatPlace(filters: SelectedFilters): String? {
    if (filters.country == null && filters.region == null) {
        return null
    }

    var result = "${filters.country?.name}"

    if (filters.region != null) {
        result += ", ${filters.region.name}"
    }

    return result
}

fun <T : Serializable?> getSerializable(bundle: Bundle, name: String, clazz: Class<T>): T? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getSerializable(name, clazz)!!
        } else {
            bundle.getSerializable(name) as T
        }
    } catch (_: NullPointerException) {
        null
    }
}

fun formatSalary(itemView: View, salaryFrom: Int?, salaryTo: Int?, salaryCurr: String): String {
    if (salaryFrom == null && salaryTo == null) {
        return itemView.context.getString(R.string.salary_not_specified)
    }

    val formattedFrom = salaryFrom?.let { "%,d".format(it).replace(',', ' ') }
    val formattedTo = salaryTo?.let { "%,d".format(it).replace(',', ' ') }

    return when {
        formattedFrom != null && formattedTo != null ->
            itemView.context.getString(
                R.string.salary_range,
                formattedFrom,
                formattedTo,
                getCurrSymbol(itemView.context, salaryCurr)
            )

        formattedFrom != null ->
            itemView.context.getString(
                R.string.salary_from,
                formattedFrom,
                getCurrSymbol(itemView.context, salaryCurr)
            )

        else ->
            itemView.context.getString(
                R.string.salary_to,
                formattedTo,
                getCurrSymbol(itemView.context, salaryCurr)
            )
    }
}
