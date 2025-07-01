package ru.practicum.android.diploma.ui.main.adapters

import androidx.recyclerview.widget.DiffUtil
import ru.practicum.android.diploma.domain.vacancy.models.Vacancy

class SearchListCallback(
    private val oldList: List<Vacancy>,
    private val newList: List<Vacancy>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val (id, name) = oldList[oldItemPosition]
        val (id1, name1) = newList[newItemPosition]
        return id == id1 && name == name1
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}
