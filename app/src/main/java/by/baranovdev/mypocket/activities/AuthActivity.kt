package by.baranovdev.mypocket.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import by.baranovdev.mypocket.activities.viewmodels.AuthViewModel
import by.baranovdev.mypocket.activities.viewmodels.AuthViewModelFactory
import by.baranovdev.mypocket.databinding.ActivityAuthBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthActivity : AppCompatActivity() {

    val auth = Firebase.auth
    lateinit var binding: ActivityAuthBinding
    val viewModel by lazy { AuthViewModelFactory(auth).create(AuthViewModel::class.java) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref:SharedPreferences = getSharedPreferences("APP_PREFERENCES", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        binding.authButtonLogin.setOnClickListener {
            viewModel.signIn(binding.authInputEmail.editText?.text.toString(), binding.authInputPassword.editText?.text.toString())
        }

        binding.authButtonRegistration.setOnClickListener {
            viewModel.signUp(binding.authInputEmail.editText?.text.toString(), binding.authInputPassword.editText?.text.toString())
        }

        binding.authInputEmail.editText?.doOnTextChanged { text, start, count, after ->
            viewModel.email = text.toString()
        }


        binding.authInputPassword.editText?.doOnTextChanged { text, start, count, after ->
            viewModel.email = text.toString()
        }

        viewModel.requestCodeLiveData.observe(this){
            val bundle = Bundle()
            when(it){
                viewModel.SIGN_IN_REQUEST_CODE_DONE ->{
                    Snackbar.make(binding.root,"Добрый день!", Snackbar.LENGTH_LONG).show()
                    editor?.putString("UID_EXTRA", auth.currentUser?.uid)
                    bundle.putString("EMAIL_EXTRA", viewModel.email)
                    sharedPref.edit().putBoolean("NEW_AUTH_FLAG", true).apply()
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent, bundle)
                }

                viewModel.SIGN_IN_REQUEST_CODE_ERROR ->{
                    Snackbar.make(binding.root,"Что-то пошло не так", Snackbar.LENGTH_LONG).show()
                }

                viewModel.SIGN_UP_REQUEST_CODE_DONE -> {
                    Snackbar.make(binding.root,"Добрый день!", Snackbar.LENGTH_LONG).show()
                    editor?.putString("UID_EXTRA", auth.currentUser?.uid)
                    bundle.putString("EMAIL_EXTRA", viewModel.email)
                    bundle.putBoolean("NEW_AUTH_FLAG", true)
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent, bundle)
                }

                viewModel.SIGN_UP_REQUEST_CODE_ERROR ->{
                    Snackbar.make(binding.root,"Что-то пошло не так", Snackbar.LENGTH_LONG).show()
                }
                else -> {}
            }
        }



    }
}