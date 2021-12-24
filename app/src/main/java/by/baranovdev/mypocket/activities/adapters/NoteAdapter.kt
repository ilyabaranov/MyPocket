package by.baranovdev.mypocket.activities.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filter.FilterResults
import android.widget.Filterable
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import by.baranovdev.mypocket.R
import by.baranovdev.mypocket.database.entity.Category
import by.baranovdev.mypocket.database.entity.Note
import by.baranovdev.mypocket.databinding.NoteItemBinding
import java.util.*
import kotlin.collections.ArrayList

class NoteAdapter(
    private val list: ArrayList<Note>,
    private val category: List<Category>,
    private val listener: OnNoteCLickListener
) :
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>(),
    Filterable {

    val fullList = list

    private lateinit var binding: NoteItemBinding

    class NoteViewHolder(
        private val binding: NoteItemBinding,
        private val listener: OnNoteCLickListener,
        val listNote: List<Note>
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            binding.title.text = listNote[adapterPosition].description
            binding.moneySpent.text = listNote[adapterPosition].money.toString()
            binding.category.text = listNote[adapterPosition].category
            binding.categoryMark.setImageResource(findIconResource(listNote[adapterPosition].category))
        }

        init {
            itemView.setOnClickListener {
                listener.onNoteCLick(listNote[adapterPosition])
            }
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        binding = NoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding, listener, list)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val current = list[position]
        holder.bind()
    }


    override fun getItemCount(): Int {
        return list.size
    }

    override fun getFilter(): Filter {
        return filter
    }

    private val filter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredList: ArrayList<Note> = ArrayList()
            if (constraint.isEmpty()) {
                filteredList.addAll(fullList)
            } else {
                val filterPattern =
                    constraint.toString().lowercase(Locale.getDefault()).trim { it <= ' ' }
                for (note in fullList) {
                    if (note.description.lowercase(Locale.getDefault()).contains(filterPattern)) {
                        filteredList.add(note)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            list.clear()
            list.addAll(results.values as java.util.ArrayList<Note>)
            notifyDataSetChanged()
        }
    }

    interface OnNoteCLickListener {
        fun onNoteCLick(note: Note?)
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

