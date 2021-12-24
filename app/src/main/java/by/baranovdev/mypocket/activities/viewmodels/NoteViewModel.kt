package by.baranovdev.mypocket.activities.viewmodels

import androidx.lifecycle.*
import by.baranovdev.mypocket.R
import by.baranovdev.mypocket.application.MyPocketApplication
import by.baranovdev.mypocket.database.entity.Category
import by.baranovdev.mypocket.database.entity.Note
import by.baranovdev.mypocket.repository.CategoryRepository
import by.baranovdev.mypocket.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
            _deletedLiveData.postValue(true)
        }
    }

    fun findIconResource(category :String):Int{
        return when(category.trim()){
            "Транспорт" -> R.drawable.ic_baseline_directions_car_24
            "Продукты" -> R.drawable.ic_baseline_fastfood_24
            "Развлечения" -> R.drawable.ic_baseline_sports_esports_24
            "Жильё" -> R.drawable.ic_baseline_house_24
            "Спорт" -> R.drawable.ic_baseline_fitness_center_24
            "Хобби" -> R.drawable.ic_baseline_emoji_emotions_24
            "Гигиена" -> R.drawable.ic_baseline_bathtub_24
            "Питомцы" -> R.drawable.ic_baseline_bedroom_baby_24
            "Отношения" -> R.drawable.ic_baseline_favorite_24
            else -> 0
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

