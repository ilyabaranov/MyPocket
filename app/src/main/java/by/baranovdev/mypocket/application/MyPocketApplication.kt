package by.baranovdev.mypocket.application

import android.app.Application
import by.baranovdev.mypocket.database.MyPocketDatabase
import by.baranovdev.mypocket.repository.CategoryRepository
import by.baranovdev.mypocket.repository.NoteRepository

class MyPocketApplication : Application() {

    val database by lazy { MyPocketDatabase.getDatabase(this) }
    val noteRepository by lazy { NoteRepository(database.noteDao()) }
    val categoryRepository by lazy { CategoryRepository(database.categoryDao()) }

}