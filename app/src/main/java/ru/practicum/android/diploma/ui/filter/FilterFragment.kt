package ru.practicum.android.diploma.ui.filter

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFilterBinding
import ru.practicum.android.diploma.ui.filter.model.FilterScreenState
import ru.practicum.android.diploma.ui.filter.model.SelectedFilters
import ru.practicum.android.diploma.ui.filter.place.COUNTRY_KEY
import ru.practicum.android.diploma.ui.filter.place.PlaceFilterFragment
import ru.practicum.android.diploma.ui.filter.place.REGION_KEY
import ru.practicum.android.diploma.ui.filter.place.models.Country
import ru.practicum.android.diploma.ui.filter.place.models.Region
import ru.practicum.android.diploma.ui.root.BindingFragment
import ru.practicum.android.diploma.ui.root.RootActivity
import ru.practicum.android.diploma.util.formatPlace

class FilterFragment : BindingFragment<FragmentFilterBinding>() {

    private val viewModel: FilterViewModel by viewModel()
    private val args by navArgs<FilterFragmentArgs>()

    private var salaryTextWatcher: TextWatcher? = null

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFilterBinding {
        return FragmentFilterBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.topbar.btnFirst.setOnClickListener {
            closeFragment(true)
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            closeFragment(true)
        }

        // Навигация к выбору страны/региона
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Country?>(COUNTRY_KEY)
            ?.observe(viewLifecycleOwner) { country ->
                viewModel.setCountry(country)
                viewModel.saveFilters()
            }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Region?>(REGION_KEY)
            ?.observe(viewLifecycleOwner) { region ->
                viewModel.setRegion(region)
                viewModel.saveFilters()
            }

        initScreen()
        viewModel.screenInit(binding, requireContext())
        viewModel.onCreate()

        args.selectedIndustryId?.let { id ->
            val name = args.selectedIndustryName
            if (name != null) {
                viewModel.setIndustry(id, name)
            }
            viewModel.saveFilters()
        }

        viewModel.getState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is FilterScreenState.CONTENT -> showContent(state.value)
            }
        }
        viewModel.getIsFiltersChanged().observe(viewLifecycleOwner) { isChanged ->
            binding.includedBtnSet.root.isVisible = isChanged
        }

        viewModel.getIsFiltersDefault().observe(viewLifecycleOwner) { isDefault ->
            binding.includedBtnCancel.root.isVisible = !isDefault
        }
        binding.includedSalary.textFieldEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.includedSalary.textFieldClear.isVisible = p0.toString().isNotEmpty()
            }

            override fun afterTextChanged(p0: Editable?) = Unit
        })
    }

    private fun closeFragment(barVisibility: Boolean) {
        (activity as RootActivity).setNavBarVisibility(barVisibility)
        findNavController().popBackStack()
    }

    private fun initScreen() {
        initListeners()
        initListenersSalaryAndBtns()
    }

    private fun initListeners() {
        binding.includedPlace.root.setOnClickListener {
            val state = viewModel.getState().value
            viewModel.saveFilters()
            val content = (state as? FilterScreenState.CONTENT)?.value
            findNavController().navigate(
                R.id.action_filterFragment_to_placeFilterFragment,
                PlaceFilterFragment.createArgs(content?.country, content?.region)
            )
        }
        binding.includedPlace.itemIcon.setOnClickListener {
            if (binding.includedPlace.itemText.text.isNotEmpty()) {
                binding.includedSalary.textFieldEdit.clearFocus()
                viewModel.clearPlace()
                viewModel.saveFilters()
            }
        }
        binding.includedIndustry.root.setOnClickListener {
            viewModel.saveFilters()
            val currentIndustryId = (viewModel.getState().value as? FilterScreenState.CONTENT)?.value?.industryId
            val action = FilterFragmentDirections.actionFilterFragmentToIndustryFilterFragment(currentIndustryId)
            findNavController().navigate(action)
        }
        binding.includedIndustry.itemIcon.setOnClickListener {
            if (binding.includedIndustry.itemText.text.isNotEmpty()) {
                binding.includedSalary.textFieldEdit.clearFocus()
                viewModel.clearIndustry()
                viewModel.saveFilters()
            }
        }
        binding.includedSalary.textFieldClear.setOnClickListener {
            viewModel.clearSalary()
            viewModel.saveFilters()
        }
        binding.includedShowNoSalary.checkbox.setOnClickListener {
            binding.includedSalary.textFieldEdit.clearFocus()
            viewModel.setShowNoSalary()
            viewModel.saveFilters()
        }
        salaryTextWatcher = binding.includedSalary.textFieldEdit.addTextChangedListener { editable ->
            val newText = editable.toString().toIntOrNull()
            if (viewModel.getCurrentSalary() != newText) {
                viewModel.setSalary(newText)
                viewModel.saveFilters()
            }
        }
    }

    private fun initListenersSalaryAndBtns() {
        binding.includedSalary.textFieldEdit.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                v.clearFocus()
                val imm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                viewModel.setSalary(binding.includedSalary.textFieldEdit.text.toString().toIntOrNull())
                viewModel.saveFilters()
                true
            } else {
                false
            }
        }

        binding.includedSalary.textFieldEdit.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.includedSalary.textFieldHeader.text = getString(R.string.expected_salary)
                binding.includedSalary.textFieldHeader.setTextColor(requireContext().getColor(R.color.blue))
            } else {
                if (binding.includedSalary.textFieldEdit.text.isEmpty()) {
                    binding.includedSalary.textFieldHeader.text = ""
                }
                viewModel.setSalary(binding.includedSalary.textFieldEdit.text.toString().toIntOrNull())
                viewModel.saveFilters()
                binding.includedSalary.textFieldHeader.setTextColor(requireContext().getColor(R.color.black))
            }
        }

        binding.includedBtnSet.root.setOnClickListener {
            viewModel.setSalary(binding.includedSalary.textFieldEdit.text.toString().toIntOrNull())
            viewModel.saveFilters()
            findNavController().popBackStack()
        }

        binding.includedBtnCancel.root.setOnClickListener {
            viewModel.clearFilters()
            viewModel.saveFilters()
        }
    }

    private fun showContent(filters: SelectedFilters) {
        val place = formatPlace(filters)
        fillPlace(place)
        fillIndustry(filters.industry)
        fillSalary(filters.salary)
        setShowNoSalary(filters.onlyWithSalary)
    }

    private fun fillPlace(place: String?) {
        val hasValue = !place.isNullOrEmpty()
        binding.includedPlace.apply {
            itemTextTop.isVisible = hasValue
            itemIcon.setImageResource(if (hasValue) R.drawable.close_24px else R.drawable.arrow_forward_24px)
            itemText.text = place ?: ""
        }
    }

    private fun fillIndustry(industry: String?) {
        val hasValue = !industry.isNullOrEmpty()
        binding.includedIndustry.apply {
            itemTextTop.isVisible = hasValue
            itemIcon.setImageResource(if (hasValue) R.drawable.close_24px else R.drawable.arrow_forward_24px)
            itemText.text = industry ?: ""
        }
    }

    private fun fillSalary(salary: Int?) {
        binding.includedSalary.apply {
            textFieldEdit.removeTextChangedListener(salaryTextWatcher)

            val currentText = textFieldEdit.text.toString()
            val newText = salary?.toString() ?: ""

            if (currentText != newText) {
                val selectionStart = textFieldEdit.selectionStart

                textFieldEdit.setText(newText)

                val newLength = textFieldEdit.text?.length ?: 0
                val newSelection = minOf(selectionStart, newLength)
                textFieldEdit.setSelection(newSelection)
            }

            if (salary != null) {
                textFieldHeader.text = getString(R.string.expected_salary)
                textFieldClear.isVisible = true
            } else {
                textFieldHeader.text = ""
                textFieldClear.isVisible = false
            }

            textFieldEdit.addTextChangedListener(salaryTextWatcher)
        }
    }

    private fun setShowNoSalary(show: Boolean) {
        binding.includedShowNoSalary.checkbox.setImageResource(
            if (show) R.drawable.check_box_on__24px else R.drawable.check_box_off__24px
        )
    }
}
