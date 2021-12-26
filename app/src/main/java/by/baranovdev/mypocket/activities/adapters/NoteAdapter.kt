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
    private val list:ArrayList<Note>,
    private val category: List<Category>,
    private val listener: OnNoteCLickListener
) :
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    companion object{
        const val NEW_NOTE_CARD = "newnotecard&kdhk6djsfk"
    }

    private lateinit var binding: NoteItemBinding

    class NoteViewHolder(
        private val binding: NoteItemBinding,
        private val listener: OnNoteCLickListener,
        val listNote: List<Note>
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            if(listNote[adapterPosition].description == NEW_NOTE_CARD && listNote[adapterPosition].category == NEW_NOTE_CARD){
                bindNewCard()
            }
            else{
                binding.title.text = listNote[adapterPosition].description
                binding.moneySpent.text = listNote[adapterPosition].money.toString()
                binding.category.text = listNote[adapterPosition].category
                binding.categoryMark.setImageResource(findIconResource(listNote[adapterPosition].category))
            }
        }

        private fun bindNewCard(){
            binding.layoutNewCard.visibility = View.VISIBLE
            binding.layoutCard.visibility = View.GONE
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


    interface OnNoteCLickListener {
        fun onNoteCLick(note: Note?)
    }
}

fun findIconResource(category :String):Int{
    return when(category.trim()){
        "Транспорт" -> R.drawable.ic_transport_24
        "Продукты" -> R.drawable.ic_products_24
        "Развлечения" -> R.drawable.ic_baseline_sports_esports_24
        "Жильё" -> R.drawable.ic_rent_24
        "Спорт" -> R.drawable.ic_sport_24
        "Хобби" -> R.drawable.ic_baseline_emoji_emotions_24
        "Гигиена" -> R.drawable.ic_hygiene_24
        "Питомцы" -> R.drawable.ic_pet_24
        "Подарки" -> R.drawable.ic_gift_24
        "Кафе" -> R.drawable.ic_category_food
        "Такси" -> R.drawable.ic_taxi_24
        "Счета" -> R.drawable.ic_is_bills_24
        "Связь" -> R.drawable.ic_telecom_24
        "Одежда" -> R.drawable.ic_clothes_24
        "Здоровье" -> R.drawable.ic_health_24
        "Авто" -> R.drawable.ic_auto_24
        else -> 0
    }


}

