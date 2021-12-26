package by.baranovdev.mypocket.realtimedb

import android.content.Context
import by.baranovdev.mypocket.database.entity.Note
import by.baranovdev.mypocket.repository.NoteRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

fun loadNotes(uid: String, repository: NoteRepository) {

    val list = ArrayList<Note>()
    val ref = Firebase.database.getReference("users/$uid/notes")

    ref.addValueEventListener(object : ValueEventListener {

        override fun onDataChange(snapshot: DataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            val value = snapshot.value
            if (value != null) {
                for (note in value as HashMap<String,Map<String, String>>) {
                    repository.insert(parseMapToNote(note.value))
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {

        }

    })
}

fun parseMapToNote(map: Map<String, String>): Note {
    var title: String = ""
    var money: Float = 0f
    var category: String = ""
    var timestamp: Long = 0L

    for (key in map.keys) {
        when (key) {
            "description" -> title = map[key].toString()
            "money" -> {
                if (map[key] != null) money = map[key].toString().toFloat()
            }
            "category" -> category = map[key].toString()
            "timestamp" -> {
                if (map[key] != null) timestamp = map[key].toString().toLong()
            }

        }
    }

    return Note(title, money, category, timestamp)
}
