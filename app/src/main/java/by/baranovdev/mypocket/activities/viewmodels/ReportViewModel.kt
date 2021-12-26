package by.baranovdev.mypocket.activities.viewmodels

import androidx.lifecycle.*
import by.baranovdev.mypocket.database.entity.Note
import by.baranovdev.mypocket.repository.NoteRepository
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.android.gms.common.util.ArrayUtils.toArrayList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAccessor
import java.util.*
import java.util.stream.Collectors
import java.util.stream.IntStream
import kotlin.collections.ArrayList

class ReportViewModel(
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _allNotes = MutableLiveData<List<Note>>()
    val allNotes: LiveData<List<Note>> = _allNotes

    var chartLabels = ArrayList<String>()

    fun allNotesReload() {
        viewModelScope.launch(Dispatchers.IO) {
            _allNotes.postValue(noteRepository.getList())
        }
    }



    private val _barDataLiveData = MutableLiveData<BarData>()
    val barDataLiveData: LiveData<BarData> = _barDataLiveData

    fun updateChartData(list: ArrayList<Note>,category:String, startDate:Long, endDate:Long) {

        val barEntryList = ArrayList<BarEntry>()
        val filteredList = ArrayList<Note>()

        for (note in list) {
            if (note.timestamp > startDate && note.timestamp < endDate + 86000000) {
                filteredList.add(note)
            }
        }
        var index = 1
        val dates = getDatesBetween(startDate, endDate)
        val labels = ArrayList<String>()
        for (date in dates as ArrayList) {
            var sum = 0f
            val dayRangeInMillis: Pair<Long?, Long?>
            labels.add("${date.dayOfMonth-1} ${getMonthFromNumber(date.monthValue)}")
            if (dates.indexOf(date) + 1 < dates.size) {
                dayRangeInMillis = Pair(
                    date.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli(),
                    dates[dates.indexOf(date) + 1].atStartOfDay(ZoneOffset.UTC)?.toInstant()
                        ?.toEpochMilli()
                )

            }
            else{
                dayRangeInMillis = Pair(
                    date.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli(),
                    (date.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli())?.plus(86000000)
                )
                labels.add("${date.dayOfMonth} ${getMonthFromNumber(date.monthValue)}")
            }
            for (note in filteredList) {
                if (note.timestamp > dayRangeInMillis.first ?: 0L && note.timestamp < dayRangeInMillis.second ?: 9999999999999L) {
                    if(category == "- ВСЕ -") sum += note.money
                    else if (note.category == category) sum+= note.money
                }
            }
            barEntryList.add(BarEntry(index.toFloat(), sum))
            index++
        }

        val barDataSet = BarDataSet(barEntryList, "LABEL")
        chartLabels = labels
        _barDataLiveData.value = BarData(barDataSet)

    }
}

private fun getMonthFromNumber(num:Int):String{
    return when(num){
        1 -> "янв"
        2 -> "фев"
        3 -> "мар"
        4 -> "апр"
        5 -> "май"
        6 -> "июн"
        7 -> "июл"
        8 -> "авг"
        9 -> "сен"
        10 -> "окт"
        11 -> "ноя"
        12 -> "дек"
        else -> ""
    }
}

private fun getDatesBetween(
    start: Long, end: Long
): List<LocalDate> {
    val startDate = Instant.ofEpochMilli(start).atZone(ZoneId.systemDefault()).toLocalDate()
    val endDate = Instant.ofEpochMilli(end).atZone(ZoneId.systemDefault()).toLocalDate()
    val listOfMillis = ArrayList<Long>()
    val numOfDaysBetween: Long = ChronoUnit.DAYS.between(startDate, endDate)
    val listOfLocalDates = IntStream.iterate(0) { i -> i + 1 }
        .limit(numOfDaysBetween)
        .mapToObj { i -> startDate.plusDays(i.toLong()) }
        .collect(Collectors.toList())
    return listOfLocalDates
}


class ReportViewModelFactory(
    private val noteRepository: NoteRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReportViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReportViewModel(noteRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}