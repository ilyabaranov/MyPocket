package by.baranovdev.mypocket.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import by.baranovdev.mypocket.database.entity.Category
import by.baranovdev.mypocket.database.entity.Note

@Dao
interface CategoryDao {

    @Insert
    fun insert(category: Category)

    @Query("SELECT * FROM category_table")
    fun getAll(): LiveData<List<Category>>
}