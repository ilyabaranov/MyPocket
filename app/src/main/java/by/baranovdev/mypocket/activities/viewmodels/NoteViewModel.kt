package by.baranovdev.mypocket.activities.viewmodels

import androidx.lifecycle.*
import by.baranovdev.mypocket.R
import by.baranovdev.mypocket.application.MyPocketApplication
import by.baranovdev.mypocket.database.entity.Category
import by.baranovdev.mypocket.database.entity.Note
import by.baranovdev.mypocket.repository.CategoryRepository
import by.baranovdev.mypocket.repository.NoteRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId

class NoteViewModel(
    private val categoryRepository: CategoryRepository,
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _currentNoteLiveData: MutableLiveData<Note?> = MutableLiveData()
    val currentNoteLiveData: LiveData<Note?> = _currentNoteLiveData


    private val _deletedLiveData: MutableLiveData<Boolean> = MutableLiveData(false)
    val deletedLiveData: LiveData<Boolean> = _deletedLiveData

    val allCategories: LiveData<List<Category>> = categoryRepository.getAll()

    var application:MyPocketApplication? = null

    fun getNote(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val note = noteRepository.getNoteById(id)
            _currentNoteLiveData.postValue(note)
        }
    }

    fun deleteNote(id:Int){
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.delete(id)
            val uid = Firebase.auth.currentUser?.uid
            val note = currentNoteLiveData.value
            val noteChild = FirebaseDatabase.getInstance().reference.child("users").child("$uid").child("notes").child("${note?.timestamp}")
            noteChild.removeValue()
            _deletedLiveData.postValue(true)
        }
    }

    fun findIconResource(category :String):Int {
        return when (category.trim()) {
            "Транспорт" -> R.drawable.ic_transport_24
            "Продукты" -> R.drawable.ic_products_24
            "Развлечения" -> R.drawable.ic_baseline_sports_esports_24
            "Жильё" -> R.drawable.ic_rent_24
            "Спорт" -> R.drawable.ic_sport_24
            "Хобби" -> R.drawable.ic_baseline_emoji_emotions_24
            "Гигиена" -> R.drawable.ic_hygiene_24
            "Питомцы" -> R.drawable.ic_pet_24
            "Подарки" -> R.drawable.ic_gift_24
            "Кафе" -> R.drawable.ic_category_food
            "Такси" -> R.drawable.ic_taxi_24
            "Счета" -> R.drawable.ic_is_bills_24
            "Связь" -> R.drawable.ic_telecom_24
            "Одежда" -> R.drawable.ic_clothes_24
            "Здоровье" -> R.drawable.ic_health_24
            "Авто" -> R.drawable.ic_auto_24
            else -> 0
        }
    }

    fun parseDate(millis :Long):String{
        val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime()

        return "${addZero(date.hour)}:${addZero(date.minute)}:${addZero(date.second)}    ${addZero(date.dayOfMonth)} ${findMonthByNumber(date.monthValue)} ${date.year}"
    }

    private fun addZero(num :Int):String{
        return if(num<10){
            "0$num"
        } else "$num"
    }

    private fun findMonthByNumber(num :Int):String{
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
}

class NoteViewModelFactory(
    private val categoryRepository: CategoryRepository,
    private val noteRepository: NoteRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(categoryRepository,noteRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

