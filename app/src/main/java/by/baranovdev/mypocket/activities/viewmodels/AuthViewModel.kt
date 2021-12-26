package by.baranovdev.mypocket.activities.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import by.baranovdev.mypocket.database.entity.Category
import by.baranovdev.mypocket.repository.CategoryRepository
import by.baranovdev.mypocket.repository.NoteRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class AuthViewModel(
    private val firebaseAuth : FirebaseAuth
) : ViewModel() {

    var email = ""
    var password = ""

    private val _requestCodeLiveData : MutableLiveData<String> = MutableLiveData()
    val requestCodeLiveData :LiveData<String> = _requestCodeLiveData

    fun signUp(cEmail:String, cPassword:String){
        firebaseAuth.createUserWithEmailAndPassword(cEmail,cPassword).addOnSuccessListener {
            _requestCodeLiveData.value = SIGN_UP_REQUEST_CODE_DONE
        }
            .addOnFailureListener {
                _requestCodeLiveData.value = SIGN_UP_REQUEST_CODE_ERROR
            }
            .addOnCanceledListener {
                _requestCodeLiveData.value = SIGN_UP_REQUEST_CODE_ERROR
            }
    }

    fun signIn(cEmail:String, cPassword:String){
        firebaseAuth.signInWithEmailAndPassword(cEmail,cPassword).addOnSuccessListener {
            _requestCodeLiveData.value = SIGN_IN_REQUEST_CODE_DONE
        }
            .addOnFailureListener {
                _requestCodeLiveData.value = SIGN_IN_REQUEST_CODE_ERROR
            }
            .addOnCanceledListener {
                _requestCodeLiveData.value = SIGN_IN_REQUEST_CODE_ERROR
            }
    }


    val SIGN_UP_REQUEST_CODE_DONE = "signupDone"
    val SIGN_UP_REQUEST_CODE_ERROR = "signupError"
    val SIGN_IN_REQUEST_CODE_DONE = "signinDone"
    val SIGN_IN_REQUEST_CODE_ERROR = "signinError"

}
class AuthViewModelFactory(
    private val firebaseAuth: FirebaseAuth
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(firebaseAuth) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

