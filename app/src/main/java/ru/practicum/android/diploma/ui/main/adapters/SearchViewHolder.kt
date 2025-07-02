package ru.practicum.android.diploma.ui.main.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.domain.vacancy.models.Vacancy
import ru.practicum.android.diploma.ui.common.dpToPx
import ru.practicum.android.diploma.util.formatSalary

class SearchViewHolder private constructor(itemView: View) : ViewHolder(itemView) {
    private val title: TextView = itemView.findViewById(R.id.title)
    private val employeTitle: TextView = itemView.findViewById(R.id.employee_title)
    private val salary: TextView = itemView.findViewById(R.id.salary)
    private val artwork: ImageView = itemView.findViewById(R.id.artwork)

    constructor(parent: ViewGroup) : this(
        LayoutInflater.from(parent.context).inflate(R.layout.vacancy_view, parent, false)
    )

    fun bind(vacancy: Vacancy) {
        title.text = vacancy.title
        employeTitle.text = vacancy.employerTitle
        salary.text = formatSalary(
            itemView,
            vacancy.salaryRange?.from,
            vacancy.salaryRange?.to,
            vacancy.salaryRange?.currency ?: ""
        )

        Glide.with(itemView)
            .load(vacancy.logoUrl)
            .placeholder(R.drawable.vacancy_artwork_placeholder)
            .error(R.drawable.vacancy_artwork_placeholder)
            .fitCenter()
            .transform(RoundedCorners(dpToPx(2F, itemView.context)))
            .into(artwork)
    }

}
