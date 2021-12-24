package by.baranovdev.mypocket.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import by.baranovdev.mypocket.R
import by.baranovdev.mypocket.activities.viewmodels.MainViewModel
import by.baranovdev.mypocket.activities.viewmodels.NoteViewModel
import by.baranovdev.mypocket.activities.viewmodels.NoteViewModelFactory
import by.baranovdev.mypocket.application.MyPocketApplication
import by.baranovdev.mypocket.database.entity.Category
import by.baranovdev.mypocket.database.entity.Note
import by.baranovdev.mypocket.databinding.ActivityMainBinding
import by.baranovdev.mypocket.databinding.ActivityNoteBinding
import by.baranovdev.mypocket.databinding.NoteItemBinding
import by.baranovdev.mypocket.repository.NoteRepository
import java.util.*

class NoteActivity : AppCompatActivity() {

    private val noteViewModel: NoteViewModel by lazy {
        NoteViewModelFactory(
            (application as MyPocketApplication).categoryRepository,
            (application as MyPocketApplication).noteRepository
            ).create(
            NoteViewModel::class.java
        ).apply {
            application = this@NoteActivity.application as MyPocketApplication
        }
    }
    lateinit var binding: ActivityNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        noteViewModel.getNote(intent.getIntExtra(MainActivity.EXTRA_SELECTED_NOTE_ID, 0))

        binding.buttonDelete.setOnClickListener {
            noteViewModel.deleteNote(intent.getIntExtra(MainActivity.EXTRA_SELECTED_NOTE_ID, 0))
        }

        noteViewModel.currentNoteLiveData.observe(this) {
            if (it != null) {
                binding.noteTitle.text = it.description
                binding.noteDate.text = Date(it.timestamp).toInstant().toString()
                binding.noteMoney.text = it.money.toString()
                binding.noteCategoryIcon.setImageResource(noteViewModel.findIconResource(it.category))
            }

        }

        noteViewModel.deletedLiveData.observe(this){
            if(it) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

    }
}