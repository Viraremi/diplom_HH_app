package ru.practicum.android.diploma.domain.vacancy.models

import java.util.Objects

data class Vacancy(
    val id: String,
    val title: String,
    val employerTitle: String,
    val logoUrl: String?,
    val salaryRange: VacancySalaryRange?,
) {
    data class VacancySalaryRange(
        val currency: String,
        val from: Int?,
        val to: Int?,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other is Vacancy) {
            return other.id == this.id && other.title == this.title
        }
        return false
    }

    override fun hashCode() = Objects.hash(id, title)
}
