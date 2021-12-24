package by.baranovdev.mypocket.repository

import androidx.lifecycle.LiveData
import by.baranovdev.mypocket.database.dao.CategoryDao
import by.baranovdev.mypocket.database.dao.NoteDao
import by.baranovdev.mypocket.database.entity.Category
import by.baranovdev.mypocket.database.entity.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryRepository(private val dao : CategoryDao) {

    private val ioScope = CoroutineScope(Dispatchers.IO)

    fun insert(category: Category){
        ioScope.launch {
            dao.insert(category)
        }
    }

    fun getAll(): LiveData<List<Category>> {
        return dao.getAll()
    }

}