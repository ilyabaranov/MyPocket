package by.baranovdev.mypocket.activities.viewmodels

import androidx.lifecycle.*
import by.baranovdev.mypocket.database.entity.Category
import by.baranovdev.mypocket.database.entity.Note
import by.baranovdev.mypocket.repository.CategoryRepository
import by.baranovdev.mypocket.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddNoteViewModel() : ViewModel() {


    fun parseCategory(str: String): String {
        return when (str) {
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
            "" -> ""
            else -> "?"
        }
    }

}

class AddNoteViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddNoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddNoteViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}