package by.baranovdev.mypocket.activities

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContentProviderCompat.requireContext
import by.baranovdev.mypocket.R
import by.baranovdev.mypocket.databinding.ActivityChangePasswordBinding
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class ChangePasswordActivity : AppCompatActivity() {

    lateinit var binding: ActivityChangePasswordBinding
    private val firebaseUser by lazy { Firebase.auth.currentUser }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonSaveNewPassword.setOnClickListener() {
            val credential: AuthCredential = EmailAuthProvider.getCredential(
                Firebase.auth.currentUser?.email.toString(),
                binding.inputOldPassword.editText?.text.toString()
            )
            val newPassword = binding.inputNewPassword.editText?.text.toString()
            if (newPassword != binding.inputNewPasswordConfirmation.editText?.text.toString()) {
                binding.inputNewPassword.error = "Пароли не совпадают"
                binding.inputNewPasswordConfirmation.error = "Пароли не совпадают"
            }

            firebaseUser?.reauthenticate(credential)?.addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = Firebase.auth.currentUser
                    user?.updatePassword(newPassword)?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            val bundle = Bundle()
                            bundle.putString("FRAGMENT_EXTRA", "settings")
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent, bundle)
                        }
                    }
                }else{
                    binding.inputOldPassword.error = "Неверный пароль"
                }
            }

        }

    }
}