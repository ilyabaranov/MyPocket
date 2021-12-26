package by.baranovdev.mypocket.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.widget.doOnTextChanged
import by.baranovdev.mypocket.R
import by.baranovdev.mypocket.activities.adapters.NoteAdapter
import by.baranovdev.mypocket.activities.adapters.findIconResource
import by.baranovdev.mypocket.database.entity.Note
import by.baranovdev.mypocket.databinding.ActivityAddNoteBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import com.jaredrummler.android.colorpicker.ColorShape
import java.util.*


class AddNoteActivity : AppCompatActivity(), ColorPickerDialogListener {
    private lateinit var binding: ActivityAddNoteBinding

    private val resultIntent: Intent
        get() = Intent().apply {
            putExtra(EXTRA_OUTPUT_TITLE, binding.inputTitle.editText?.text.toString())
            putExtra(EXTRA_OUTPUT_MONEY, binding.inputMoney.editText?.text.toString().toFloat())
            putExtra(EXTRA_OUTPUT_CATEGORY, binding.inputCategory.editText?.text.toString())
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uid = Firebase.auth.uid

        val selectedCategory = intent.getStringExtra(EXTRA_INPUT_SELECTED_CATEGORY) ?: ""

        binding.exampleCardCategoryIcon.visibility = View.INVISIBLE

        if (selectedCategory.isNotEmpty()) {
            binding.inputCategory.editText?.setText(selectedCategory)
            binding.exampleCardCategory.text = selectedCategory
            binding.exampleCardCategoryIcon.visibility = View.VISIBLE
            binding.exampleCardCategoryIcon.setImageResource(findIconResource(selectedCategory))
        }

        val database = Firebase.database
        val myRef = database.getReference("users/$uid")

        val adapter = ArrayAdapter(
            application,
            R.layout.list_item,
            arrayOf("Транспорт", "Продукты", "Развлечения", "Спорт", "Гигиена", "Жильё", "Такси", "Авто", "Кафе", "Питомцы", "Подарки", "Счета", "Одежда", "Здоровье", "Связь", "Хобби")
        )
        (binding.inputCategory.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        binding.buttonSave.setOnClickListener { onSavePressed() }

        binding.inputTitle.editText?.doOnTextChanged { text, start, count, after ->
            binding.exampleCardTitle.text = text.toString()
        }
        binding.inputCategory.editText?.doOnTextChanged { text, start, count, after ->
            binding.exampleCardCategory.text = text.toString()
            binding.exampleCardCategoryIcon.visibility = View.VISIBLE
            binding.exampleCardCategoryIcon.setImageResource(findIconResource(text.toString()))
        }
        binding.inputMoney.editText?.doOnTextChanged { text, start, count, after ->
            binding.exampleCardMoneySpent.text = text.toString()
        }

    }

    private fun createColorPickerDialog() {
        ColorPickerDialog.newBuilder()
            .setColor(Color.RED)
            .setDialogType(ColorPickerDialog.TYPE_PRESETS)
            .setAllowCustom(true)
            .setAllowPresets(true)
            .setColorShape(ColorShape.SQUARE)
            .show(this)
    }

    override fun onBackPressed() {
        setResult(RESULT_CANCELED, resultIntent)
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun onSavePressed() {
        setResult(RESULT_OK, resultIntent)
        finish()
    }


    class Contract : ActivityResultContract<String?, Note?>() {

        override fun createIntent(context: Context, input: String?): Intent {
            return if (input != null) Intent(context, AddNoteActivity::class.java).apply {
                putExtra(EXTRA_INPUT_SELECTED_CATEGORY, input)
            } else Intent(context, AddNoteActivity::class.java)

        }

        override fun parseResult(resultCode: Int, intent: Intent?): Note? {
            if (intent == null) return null
            val timestamp = Calendar.getInstance().timeInMillis
            val title = intent.getStringExtra(EXTRA_OUTPUT_TITLE) ?: return null
            val money = intent.getFloatExtra(EXTRA_OUTPUT_MONEY, 0f)
            val category = intent.getStringExtra(EXTRA_OUTPUT_CATEGORY) ?: return null
            val confirmed = resultCode == RESULT_OK
            return Note(title, money, category, timestamp)
        }

    }

    companion object {
        private const val EXTRA_INPUT_CATEGORIES = "EXTRA_CATEGORIES"
        private const val EXTRA_INPUT_SELECTED_CATEGORY = "EXTRA_CATEGORY"
        private const val EXTRA_OUTPUT_TITLE = "EXTRA_TITLE"
        private const val EXTRA_OUTPUT_MONEY = "EXTRA_MONEY"
        private const val EXTRA_OUTPUT_CATEGORY = "EXTRA_CATEGORY"
    }

    override fun onColorSelected(dialogId: Int, color: Int) {

    }

    override fun onDialogDismissed(dialogId: Int) {
        TODO("Not yet implemented")
    }
}
