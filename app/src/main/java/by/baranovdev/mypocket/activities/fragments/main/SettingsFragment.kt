package by.baranovdev.mypocket.activities.fragments.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import by.baranovdev.mypocket.activities.AuthActivity
import by.baranovdev.mypocket.activities.ChangePasswordActivity
import by.baranovdev.mypocket.activities.viewmodels.MainViewModel
import by.baranovdev.mypocket.activities.viewmodels.MainViewModelFactory
import by.baranovdev.mypocket.application.MyPocketApplication
import by.baranovdev.mypocket.databinding.FragmentReportBinding
import by.baranovdev.mypocket.databinding.FragmentSettingsBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class SettingsFragment : Fragment() {

    lateinit var binding: FragmentSettingsBinding
    private val viewModel by lazy {
        MainViewModelFactory((requireActivity().application as MyPocketApplication).noteRepository).create(
            MainViewModel::class.java
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.settingsEmail.text = "E-mail : ${Firebase.auth.currentUser?.email}"

        binding.buttonChangePassword.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("USER_EMAIL_EXTRA", Firebase.auth.currentUser?.email)
            val intent = Intent(requireContext(),ChangePasswordActivity::class.java)
            startActivity(intent, bundle)
        }

        binding.buttonDeleteAccount.setOnClickListener {

            FirebaseDatabase.getInstance().reference.child("users").child(Firebase.auth.currentUser?.uid.toString()).removeValue()
            Firebase.auth.currentUser?.delete()
            viewModel.deleteAll()
            val intent = Intent(requireContext(),AuthActivity::class.java)
            startActivity(intent)
        }

        binding.buttonLogout.setOnClickListener {
            Firebase.auth.signOut()
            viewModel.deleteAll()
            val intent = Intent(requireContext(),AuthActivity::class.java)
            startActivity(intent)
        }

    }

}