package by.baranovdev.mypocket.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import androidx.recyclerview.widget.LinearLayoutManager
import by.baranovdev.mypocket.activities.adapters.NoteAdapter
import by.baranovdev.mypocket.application.MyPocketApplication
import by.baranovdev.mypocket.activities.viewmodels.MainViewModel
import by.baranovdev.mypocket.activities.viewmodels.MainViewModelFactory

import by.baranovdev.mypocket.database.entity.Note
import by.baranovdev.mypocket.databinding.ActivityMainBinding
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener

import androidx.appcompat.widget.Toolbar
import android.content.Intent as Intent
import androidx.core.view.MenuItemCompat

import android.content.res.TypedArray

import android.view.animation.Animation

import android.view.animation.AnimationSet

import android.view.animation.TranslateAnimation

import android.view.animation.AlphaAnimation

import android.animation.Animator

import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.res.Resources

import android.os.Build
import android.view.*

import androidx.core.content.ContextCompat
import by.baranovdev.mypocket.R


class MainActivity : AppCompatActivity(), OnChartValueSelectedListener,
    NoteAdapter.OnNoteCLickListener {
    private lateinit var binding: ActivityMainBinding


    lateinit var mToolbar:Toolbar
    lateinit var mSearchItem: MenuItem
    lateinit var chart: PieChart

    private val viewModel by lazy {
        MainViewModelFactory((application as MyPocketApplication).noteRepository).create(
            MainViewModel::class.java
        )
    }

    private val editMessageLauncher = registerForActivityResult(AddNoteActivity.Contract()) {
        if (it != null) {
            viewModel.insert(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.fab.setOnClickListener { editMessage() }

        val chart = binding.pieChart


        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = NoteAdapter(
            viewModel.allNotes.value as ArrayList<Note>? ?: ArrayList(),
            viewModel.categories,
            this
        )

        chart.data = viewModel.updateChartData(viewModel.allNotes.value ?: emptyList())

        viewModel.allNotes.observe(this) {
            binding.recycler.adapter = NoteAdapter(
                viewModel.allNotes.value as ArrayList<Note>? ?: ArrayList(),
                viewModel.categories,
                this
            )
            chart.data = viewModel.updateChartData(it)
        }
        binding.pieChart.setOnChartValueSelectedListener(this)

        binding.recycler.adapter

    }


    private fun editMessage() {
        editMessageLauncher.launch(viewModel.currentCategoryName)
    }


    override fun onValueSelected(e: Entry?, h: Highlight?) {
        val sortedList = ArrayList<Note>()
        val entry = e as PieEntry
        val fullList = viewModel.allNotes.value
        for (note in fullList as ArrayList<Note>) {
            if (note.category.lowercase() == entry.label.toString().lowercase()) {
                sortedList.add(note)
            }
        }
        binding.recycler.adapter = NoteAdapter(sortedList, viewModel.categories, this)
        viewModel.currentCategoryName = entry.label.toString()
    }

    override fun onNothingSelected() {
        binding.recycler.adapter = NoteAdapter(
            viewModel.allNotes.value as ArrayList<Note>? ?: ArrayList(),
            viewModel.categories,
            this
        )
        viewModel.currentCategoryName = null
    }

    override fun onNoteCLick(note: Note?) {
        val intent = Intent(this, NoteActivity::class.java)
        intent.putExtra(EXTRA_SELECTED_NOTE_ID, note?.id)
        startActivity(intent)
    }



    companion object {
        val EXTRA_SELECTED_NOTE_ID = "selected_note_id"
    }
}