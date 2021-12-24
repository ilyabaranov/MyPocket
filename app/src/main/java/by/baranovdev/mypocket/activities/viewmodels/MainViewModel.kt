package by.baranovdev.mypocket.activities.viewmodels

import android.graphics.Color
import android.graphics.ColorSpace
import androidx.core.graphics.toColor
import androidx.lifecycle.*
import by.baranovdev.mypocket.R
import by.baranovdev.mypocket.database.entity.Category
import by.baranovdev.mypocket.database.entity.Note
import by.baranovdev.mypocket.repository.NoteRepository
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.launch

class MainViewModel(private val repository: NoteRepository) : ViewModel() {


    val allNotes: LiveData<List<Note>> = repository.getALl()
    val categories = ArrayList<Category>()



    var currentCategoryName:String? = null

    private val _sortType: MutableLiveData<Int> = MutableLiveData(0)
    val sortType: LiveData<Int> = _sortType

    fun insert(note: Note) = viewModelScope.launch {
        repository.insert(note)
    }

    fun updateChartData(list: List<Note>) :PieData{
        categories.clear()
        val entries = ArrayList<PieEntry>()
        var currentCategory = ""
        for(exCategory in categories){
            exCategory.moneySum = 0f
        }
        val colors = ArrayList<Int>()
        for (note in list) {
            var flag = false
            currentCategory = note.category
            if (categories.isNotEmpty()) for (exCategory in categories) {
                if (currentCategory.lowercase() == exCategory.name.lowercase()){
                    exCategory.moneySum+= note.money
                    flag = true
                    break
                }
            }
            if(!flag){
                val c = Category(note.category,note.money,Color.rgb(note.colorRed,note.colorGreen,note.colorBlue))
                categories.add(c)
                val color = findIconColor(note.category)
                colors.add(Color.rgb(color[0],color[1],color[2]))
            }
        }

        for(c in categories){
            entries.add(PieEntry(c.moneySum, c.name))
        }

        val dataSet = PieDataSet(entries, "Money spent")
        dataSet.colors = colors
        ColorTemplate.VORDIPLOM_COLORS

        return PieData(dataSet)
    }

    fun getCategoryNames():Array<String>{
        val list = ArrayList<String>()
        for(c in categories) list.add(c.name)
        return list.toTypedArray()
    }


    fun findIconColor(category: String):Array<Int>{
        return when(category.trim()){
            "Транспорт" -> arrayOf(45,54,225)
            "Продукты" -> arrayOf(255,193,7)
            "Развлечения" -> arrayOf(103,58,183)
            "Жильё" -> arrayOf(237,137,29)
            "Спорт" -> arrayOf(76,175,80)
            "Хобби" -> arrayOf(163,67,67)
            "Гигиена" -> arrayOf(65,236,162)
            "Питомцы" -> arrayOf(151,29,29)
            "Отношения" -> arrayOf(227,63,148)
            else -> arrayOf(0,0,0)
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