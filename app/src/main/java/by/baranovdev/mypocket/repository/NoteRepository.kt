package by.baranovdev.mypocket.repository

import androidx.lifecycle.LiveData
import by.baranovdev.mypocket.database.dao.NoteDao
import by.baranovdev.mypocket.database.entity.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteRepository(private val dao :NoteDao) {

    private val ioScope = CoroutineScope(Dispatchers.IO)

    fun insert(note: Note){
        ioScope.launch {
            dao.insert(note)
        }
    }

    fun getALl():LiveData<List<Note>>{
        return dao.getAll()
    }

    suspend fun getNoteById(id:Int):Note?{
        return dao.getNoteById(id)
    }

    fun delete(note: Note){
        dao.delete(note)
    }

    fun delete(id:Int){
        dao.delete(id)
    }

}