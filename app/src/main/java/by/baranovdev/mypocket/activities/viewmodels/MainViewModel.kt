package by.baranovdev.mypocket.activities.viewmodels

import android.graphics.Color
import android.graphics.ColorSpace
import androidx.core.graphics.toColor
import androidx.lifecycle.*
import by.baranovdev.mypocket.R
import by.baranovdev.mypocket.activities.adapters.NoteAdapter
import by.baranovdev.mypocket.database.entity.Category
import by.baranovdev.mypocket.database.entity.Note
import by.baranovdev.mypocket.repository.NoteRepository
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(private val repository: NoteRepository) : ViewModel() {


    val allNotes: LiveData<List<Note>> = repository.getALl()
    val categories = ArrayList<Category>()

    var startDate: Long = 0L
    var endDate: Long = 1L


    var currentCategoryName: String? = null

    private val _reportChosenCategory: MutableLiveData<String> = MutableLiveData("- ВСЕ -")
    val reportChosenCategory: LiveData<String> = _reportChosenCategory

    fun setNewReportChosenCategoryValue(str :String){
        _reportChosenCategory.value = str
    }

    private val _isLoadingFromServerReady: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoadingFromServerReady: LiveData<Boolean> = _isLoadingFromServerReady
    fun insert(note: Note) = viewModelScope.launch {
        repository.insert(note)
    }

    fun deleteAll() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAll()
        _isLoadingFromServerReady.postValue(true)
    }

    fun setIsLoadingFromServerReadyToFalse(){
        _isLoadingFromServerReady.value = false
    }


    fun updateChartData(list: List<Note>): PieData {
        categories.clear()
        val entries = ArrayList<PieEntry>()
        var currentCategory = ""
        for (exCategory in categories) {
            exCategory.moneySum = 0f
        }
        val colors = ArrayList<Int>()
        for (note in list) {
            var flag = false
            currentCategory = note.category
            if (categories.isNotEmpty()) for (exCategory in categories) {
                if (currentCategory.lowercase() == exCategory.name.lowercase() && currentCategory != NoteAdapter.NEW_NOTE_CARD) {
                    exCategory.moneySum += note.money
                    flag = true
                    break
                }
            }
            if (!flag && currentCategory != NoteAdapter.NEW_NOTE_CARD) {
                val c = Category(
                    note.category,
                    note.money
                )
                categories.add(c)
                val color = findIconColor(note.category)
                colors.add(Color.rgb(color[0], color[1], color[2]))
            }
        }

        for (c in categories) {
            entries.add(PieEntry(c.moneySum, findChartIcon(c.name)))
        }

        val dataSet = PieDataSet(entries, "Money spent")
        dataSet.colors = colors
        ColorTemplate.VORDIPLOM_COLORS

        return PieData(dataSet)
    }

    fun getCategoryNames(): Array<String> {
        val list = ArrayList<String>()
        for (c in categories) list.add(c.name)
        return list.toTypedArray()
    }


    private fun findIconColor(category: String): Array<Int> {
        return when (category.trim()) {
            "Транспорт" -> arrayOf(156, 98, 249)
            "Продукты" -> arrayOf(81, 153, 255)
            "Развлечения" -> arrayOf(255, 169, 107)
            "Жильё" -> arrayOf(169, 118, 204)
            "Спорт" -> arrayOf(243, 202, 153)
            "Хобби" -> arrayOf(168, 84, 84)
            "Гигиена" -> arrayOf(185, 225, 134)
            "Питомцы" -> arrayOf(217, 163, 125)
            "Подарки" -> arrayOf(255, 160, 160)
            "Кафе" -> arrayOf(233, 108, 173)
            "Такси" -> arrayOf(250, 210, 108)
            "Счета" -> arrayOf(252, 183, 119)
            "Связь" -> arrayOf(93, 120, 251)
            "Одежда" -> arrayOf(248, 92, 80)
            "Здоровье" -> arrayOf(248, 115, 105)
            "Авто" -> arrayOf(74, 88, 110)
            else -> arrayOf(0, 0, 0)
        }
    }

    private fun findChartIcon(category: String):String{
        return when (category.trim()) {
            "Транспорт" -> "P"
            "Продукты" -> "G"
            "Развлечения" -> "K"
            "Жильё" -> "M"
            "Спорт" -> "L"
            "Хобби" -> "I"
            "Гигиена" -> "E"
            "Питомцы" -> "F"
            "Подарки" -> "C"
            "Кафе" -> "J"
            "Такси" -> "N"
            "Счета" -> "H"
            "Связь" -> "O"
            "Одежда" -> "B"
            "Здоровье" -> "D"
            "Авто" -> "A"
            else -> "?"
        }
    }

    fun findCategoryByLabel(label :String):String{
        return when (label.trim()) {
            "P" -> "Транспорт"
            "G" -> "Продукты"
            "K" -> "Развлечения"
            "M" -> "Жильё"
            "L" -> "Спорт"
            "I" -> "Хобби"
            "E" -> "Гигиена"
            "F" -> "Питомцы"
            "C" -> "Подарки"
            "J" -> "Кафе"
            "N" -> "Такси"
            "H" -> "Счета"
            "O" -> "Связь"
            "B" -> "Одежда"
            "D" -> "Здоровье"
            "A" -> "Авто"
            else -> "?"
        }
    }
}

class MainViewModelFactory(private val repository: NoteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

enum class SortType {
    NAME, TIMESTAMP, MONEY, CATEGORY
}