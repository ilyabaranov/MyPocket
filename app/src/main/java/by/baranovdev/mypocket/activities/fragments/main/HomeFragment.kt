package by.baranovdev.mypocket.activities.fragments.main

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.baranovdev.mypocket.R
import by.baranovdev.mypocket.activities.AddNoteActivity
import by.baranovdev.mypocket.activities.NoteActivity
import by.baranovdev.mypocket.activities.adapters.NoteAdapter
import by.baranovdev.mypocket.activities.viewmodels.MainViewModel
import by.baranovdev.mypocket.activities.viewmodels.MainViewModelFactory
import by.baranovdev.mypocket.application.MyPocketApplication
import by.baranovdev.mypocket.database.entity.Note
import by.baranovdev.mypocket.databinding.ActivityMainBinding
import by.baranovdev.mypocket.databinding.FragmentHomeBinding
import by.baranovdev.mypocket.realtimedb.loadNotes
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.DecimalFormat

class HomeFragment : Fragment(), OnChartValueSelectedListener,
    NoteAdapter.OnNoteCLickListener {

    lateinit var binding: FragmentHomeBinding

    lateinit var mToolbar: Toolbar
    lateinit var mSearchItem: MenuItem
    lateinit var chart: PieChart

    val uid = Firebase.auth.currentUser?.uid
    val database = FirebaseDatabase.getInstance().reference

    private val viewModel by lazy {
        MainViewModelFactory((requireActivity().application as MyPocketApplication).noteRepository).create(
            MainViewModel::class.java
        )
    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        }
        return false
    }

    private val addNoteLauncher = registerForActivityResult(AddNoteActivity.Contract()) {
        if (it != null) {


            if(!isOnline(requireContext())){viewModel.insert(it)}
            else {
                viewModel.insert(it)
                database.child("users").child("$uid").child("notes").child("${it.timestamp}").setValue(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        super.onCreate(savedInstanceState)

        val chart = binding.pieChart

        val sharedPref = requireActivity().getSharedPreferences("APP_PREFERENCES", Context.MODE_PRIVATE)

        if (sharedPref.getBoolean("NEW_AUTH_FLAG", false)){

            sharedPref.edit().putBoolean("NEW_AUTH_FLAG", false).apply()
            val repository = (requireActivity().application as MyPocketApplication).noteRepository
            viewModel.deleteAll()

        }

        viewModel.isLoadingFromServerReady.observe(requireActivity()) {
            if (it) {
                val repository =
                    (requireActivity().application as MyPocketApplication).noteRepository
                loadNotes(Firebase.auth.currentUser?.uid ?: "", repository)
                viewModel.setIsLoadingFromServerReadyToFalse()
            }
        }


        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.adapter = NoteAdapter(
            listProcessing(viewModel.allNotes.value as ArrayList<Note>?) ?: ArrayList(),
            viewModel.categories,
            this
        )

        chart.data = viewModel.updateChartData(viewModel.allNotes.value ?: emptyList())
        chart.animateY(800, Easing.EaseInOutQuad)
        countSum()
        chart.legend.isEnabled = false
        chart.description.text = ""
        chart.setEntryLabelTextSize(24f)
        chart.setDrawRoundedSlices(false)

        val typefaceLabel = ResourcesCompat.getFont(requireContext(), R.font.icons_font)
        val typefaceCenter = ResourcesCompat.getFont(requireContext(), R.font.futura_medium_c)

        chart.setEntryLabelTypeface(typefaceLabel)
        chart.setCenterTextTypeface(typefaceCenter)

        viewModel.allNotes.observe(requireActivity()) {
            binding.recycler.adapter = NoteAdapter(
                listProcessing(viewModel.allNotes.value as ArrayList<Note>?) ?: ArrayList(),
                viewModel.categories,
                this
            )
            chart.data = viewModel.updateChartData(it)
            countSum()
        }
        binding.pieChart.setOnChartValueSelectedListener(this)

        binding.recycler.adapter

    }


    private fun addNote() {
        addNoteLauncher.launch(viewModel.currentCategoryName)
    }


    override fun onValueSelected(e: Entry?, h: Highlight?) {
        val sortedList = ArrayList<Note>()
        val entry = e as PieEntry
        var sum = 0f
        val currentCategory = viewModel.findCategoryByLabel(entry.label).lowercase()
        val fullList = viewModel.allNotes.value
        for (note in fullList as ArrayList<Note>) {
            if (note.category.lowercase() == currentCategory) {
                sortedList.add(note)
                sum += note.money
            }
        }
        binding.pieChart.centerText =
            "${DecimalFormat("#0.00").format(sum).replace(',', '.')}\n ${currentCategory}"
        binding.recycler.adapter =
            NoteAdapter(listProcessing(sortedList) ?: ArrayList(), viewModel.categories, this)
        viewModel.currentCategoryName = entry.label.toString()
    }

    override fun onNothingSelected() {
        binding.recycler.adapter = NoteAdapter(
            (viewModel.allNotes.value?.reversed() as ArrayList<Note>? ?: ArrayList()),
            viewModel.categories,
            this
        )
        countSum()
        viewModel.currentCategoryName = null
    }

    override fun onNoteCLick(note: Note?) {
        if (note == newNoteCard) {
            addNote()
        } else {
            val intent = Intent(requireContext(), NoteActivity::class.java)
            intent.putExtra(EXTRA_SELECTED_NOTE_ID, note?.id)
            startActivity(intent)
        }
    }


    companion object {
        val EXTRA_SELECTED_NOTE_ID = "selected_note_id"
        val newNoteCard = Note(NoteAdapter.NEW_NOTE_CARD, 0f, NoteAdapter.NEW_NOTE_CARD, 0)
    }


    private fun listProcessing(list: ArrayList<Note>?): ArrayList<Note>? {
        return if (list != null) {
            list.add(Note(NoteAdapter.NEW_NOTE_CARD, 0f, NoteAdapter.NEW_NOTE_CARD, 0))
            ArrayList<Note>(list.reversed())
        } else null
    }

    private fun countSum() {
        var sum = 0f
        val noteList = viewModel.allNotes.value
        if (!noteList.isNullOrEmpty()) for (note in noteList) sum += note.money
        binding.pieChart.centerText =
            "${DecimalFormat("#0.00").format(sum).replace(',', '.')}\n всего"
    }

}
