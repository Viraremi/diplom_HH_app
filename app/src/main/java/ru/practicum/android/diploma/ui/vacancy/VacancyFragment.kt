package ru.practicum.android.diploma.ui.vacancy

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentVacancyBinding
import ru.practicum.android.diploma.ui.root.BindingFragment
import ru.practicum.android.diploma.ui.root.RootActivity
import ru.practicum.android.diploma.util.HTTP_400_BAD_REQUEST
import ru.practicum.android.diploma.util.HTTP_500_INTERNAL_SERVER_ERROR
import ru.practicum.android.diploma.util.HTTP_NO_CONNECTION

class VacancyFragment : BindingFragment<FragmentVacancyBinding>() {
    private val viewModel by viewModel<VacancyViewModel>()
    private var vacancyId: String = ""
    private var currentVacancy: VacancyDetailsVO? = null

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentVacancyBinding {
        return FragmentVacancyBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vacancyId = requireArguments().getString(ARGS_ID) ?: ""

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.vacancyState.collect { state ->
                    render(state)
                }
            }
        }

        initUiToolbar()
        initPlaceholder()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            closeFragment(true)
        }
        viewModel.loadVacancyDetails(vacancyId)
    }

    private fun initPlaceholder() {
        binding.includedErr.apply {
            placeholderText.text = requireContext().getString(R.string.no_find_vacancy)
            placeholderImage.setImageResource(R.drawable.err_no_vacancy)
        }
    }

    private fun initUiToolbar() {
        binding.topbar.apply {
            btnFirst.setImageResource(R.drawable.arrow_back_24px)
            btnSecond.setImageResource(R.drawable.sharing_24px)
            btnThird.setImageResource(R.drawable.favorites_off__24px)
            header.text = requireContext().getString(R.string.vacancy)
        }

        // Назад
        binding.topbar.btnFirst.setOnClickListener {
            closeFragment(true)
        }

        // Поделиться
        binding.topbar.btnSecond.setOnClickListener {
            requireContext().startActivity(
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, currentVacancy?.url)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
        }

        // Избранное
        binding.topbar.btnThird.setOnClickListener {
            viewModel.changeFavorite()
        }
    }

    private fun closeFragment(barVisibility: Boolean) {
        (activity as RootActivity).setNavBarVisibility(barVisibility)
        findNavController().popBackStack()
    }

    private fun render(state: VacancyContentStateVO) {
        when (state) {
            is VacancyContentStateVO.Base -> showBase()
            is VacancyContentStateVO.Loading -> showLoading()
            is VacancyContentStateVO.Error -> showError(state.code)
            is VacancyContentStateVO.Success -> showVacancyDetails(state.vacancy)
            is VacancyContentStateVO.SetFavorite -> setFavoriteIcon(state.isFavorite)
        }
    }

    private fun showBase() {
        binding.vacancyName.text = ""
    }

    private fun showLoading() {
        binding.contentView.isVisible = false
        binding.includedProgressBar.progressBar.isVisible = true
        binding.includedErr.root.isVisible = false
        binding.noInternetError.isVisible = false
        binding.serverError.isVisible = false
    }

    private fun showError(code: Int) {
        binding.contentView.isVisible = false
        binding.includedProgressBar.root.isVisible = false
        binding.noInternetError.isVisible = false
        binding.serverError.isVisible = false
        binding.includedErr.root.isVisible = false
        when (code) {
            HTTP_NO_CONNECTION -> {
                binding.noInternetError.isVisible = true
            }

            HTTP_500_INTERNAL_SERVER_ERROR,
            HTTP_400_BAD_REQUEST -> {
                binding.serverError.isVisible = true
            }

            else -> {
                binding.includedErr.root.isVisible = true
            }
        }
    }

    private fun showVacancyDetails(vacancy: VacancyDetailsVO) {
        currentVacancy = vacancy

        setFavoriteIcon(vacancy.isFavorite)
        binding.apply {
            vacancyName.text = vacancy.title
            vacancySalary.text = vacancy.salary

            if (!vacancy.logoUrl.isNullOrBlank()) {
                Glide.with(requireContext())
                    .load(vacancy.logoUrl)
                    .placeholder(R.drawable.vacancy_artwork_placeholder)
                    .error(R.drawable.vacancy_artwork_placeholder)
                    .fitCenter()
                    .into(binding.includedVacancyCard.imageVacancyCard)
            } else {
                binding.includedVacancyCard.imageVacancyCard.setImageResource(R.drawable.vacancy_artwork_placeholder)
            }

            includedVacancyCard.titleVacancyCard.text = vacancy.employerName
            includedVacancyCard.cityVacancyCard.text = vacancy.addressOrRegion
            valueExp.text = vacancy.experience
            valueWorkFormat.text = vacancy.employment
            valueDescription.text = vacancy.description
            valueSkills.text = vacancy.keySkills.joinToString("\n") { "• $it" }
            headerSkills.isVisible = true
            valueSkills.isVisible = true

            includedProgressBar.root.isVisible = false
            includedErr.root.isVisible = false
            contentView.isVisible = true
            noInternetError.isVisible = false
            serverError.isVisible = false
        }
    }

    private fun setFavoriteIcon(state: Boolean) {
        binding.topbar.btnThird.setImageResource(
            if (state) {
                R.drawable.favorites_on__24px
            } else {
                R.drawable.favorites_off__24px
            }
        )
    }

    companion object {
        private const val ARGS_ID = "vacancy_id"

        fun createArgs(vacancyId: String): Bundle = bundleOf(
            ARGS_ID to vacancyId
        )
    }
}
