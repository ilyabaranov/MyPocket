package by.baranovdev.mypocket.activities.fragments.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.widget.doOnTextChanged
import by.baranovdev.mypocket.R
import by.baranovdev.mypocket.activities.viewmodels.MainViewModel
import by.baranovdev.mypocket.activities.viewmodels.MainViewModelFactory
import by.baranovdev.mypocket.activities.viewmodels.ReportViewModel
import by.baranovdev.mypocket.activities.viewmodels.ReportViewModelFactory
import by.baranovdev.mypocket.application.MyPocketApplication
import by.baranovdev.mypocket.database.entity.Note
import by.baranovdev.mypocket.databinding.FragmentReportBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.github.mikephil.charting.components.XAxis.XAxisPosition

import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter


class ReportFragment : Fragment() {

    lateinit var binding: FragmentReportBinding

    val viewModel: MainViewModel by lazy {
        MainViewModelFactory((requireActivity().application as MyPocketApplication).noteRepository).create(
            MainViewModel::class.java
        )
    }

    val reportViewModel: ReportViewModel by lazy {
        ReportViewModelFactory((requireActivity().application as MyPocketApplication).noteRepository).create(
            ReportViewModel::class.java
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentReportBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val chart = binding.barChart

        if(viewModel.startDate != 0L) {
            reportViewModel.updateChartData(
                viewModel.allNotes.value as ArrayList<Note>,
                viewModel.reportChosenCategory.value ?: "- ВСЕ -",
                viewModel.startDate,
                viewModel.endDate
            )
        }

        val adapter = ArrayAdapter(
            requireActivity(),
            R.layout.list_item,
            arrayOf("- ВСЕ -","Транспорт", "Продукты", "Развлечения", "Спорт", "Гигиена", "Жильё", "Такси", "Авто", "Кафе", "Питомцы", "Подарки", "Счета", "Одежда", "Здоровье", "Связь", "Хобби")
        )
        (binding.reportInputCategory.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        val xAxis: XAxis = chart.xAxis
        xAxis.position = XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)

        binding.reportInputCategory.editText?.doOnTextChanged { text, start, count, after ->
            viewModel.setNewReportChosenCategoryValue(text.toString())
        }

        binding.buttonChooseDate.setOnClickListener {
            val dateRangePicker =
                MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText("Select dates")
                    .build()
            dateRangePicker.addOnPositiveButtonClickListener {
                viewModel.startDate = it.first - 86000000
                viewModel.endDate = it.second + 86000000
                reportViewModel.updateChartData(viewModel.allNotes.value as ArrayList<Note>, viewModel.reportChosenCategory.value ?: "", viewModel.startDate, viewModel.endDate)
            }
            dateRangePicker.show(requireActivity().supportFragmentManager, "TAG")
        }

        viewModel.allNotes.observe(requireActivity()) {
            if(viewModel.startDate != 0L) {
                reportViewModel.updateChartData(
                    it as ArrayList<Note>,
                    viewModel.reportChosenCategory.value ?: "- ВСЕ -",
                    viewModel.startDate,
                    viewModel.endDate
                )
            }
        }

        reportViewModel.barDataLiveData.observe(requireActivity()) {
            val xAxis = binding.barChart.xAxis
            xAxis.valueFormatter = IndexAxisValueFormatter(reportViewModel.chartLabels)
            xAxis.setDrawGridLines(false)
            xAxis.position = XAxisPosition.TOP
            xAxis.setAvoidFirstLastClipping(true)
            xAxis.isGranularityEnabled = false
            xAxis.labelRotationAngle = 270f
            binding.barChart.resetZoom()
            binding.barChart.data = it
        }

        viewModel.reportChosenCategory.observe(requireActivity()){
            if(viewModel.startDate != 0L){
                reportViewModel.updateChartData(viewModel.allNotes.value as ArrayList<Note>, it, viewModel.startDate, viewModel.endDate)
            }
        }

    }

}