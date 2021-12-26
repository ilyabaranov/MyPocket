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
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import by.baranovdev.mypocket.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    val viewModel by lazy {
        MainViewModelFactory((application as MyPocketApplication).noteRepository).create(
            MainViewModel::class.java
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences("APP_PREFERENCES", Context.MODE_PRIVATE)



        if(Firebase.auth.currentUser == null && intent.getStringExtra("UID_EXTRA").isNullOrEmpty()){
            viewModel.deleteAll()
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_report, R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}