package by.baranovdev.mypocket.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import by.baranovdev.mypocket.database.entity.Note

@Dao
interface NoteDao {

    @Insert
    fun insert(note: Note)

    @Query("SELECT * FROM note_table")
    fun getAll():LiveData<List<Note>>

    @Query("SELECT * FROM note_table WHERE id=:noteId")
    fun getNoteById(noteId:Int):Note?

    @Delete
    fun delete(note:Note)

    @Query("DELETE FROM note_table WHERE id=:noteId")
    fun delete(noteId:Int)

}