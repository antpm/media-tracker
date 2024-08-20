package ca.anthony.mediatracker.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import ca.anthony.mediatracker.databinding.FragmentSignUpBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        binding.SignUpCreateAccount.setOnClickListener {
            if (binding.SignUpEmail.text.isEmpty() || binding.SignUpPass.text.isEmpty() || binding.SignUpName.text.isEmpty()){
                Toast.makeText(requireActivity(), "Please fill out all fields", Toast.LENGTH_SHORT).show()
            } else {
                val profile = userProfileChangeRequest {
                    displayName = binding.SignUpName.text.toString()
                }

                val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.US)
                val currentDate = dateFormatter.format(Date())

                val userData = hashMapOf(
                    "join" to currentDate
                )

                auth.createUserWithEmailAndPassword(binding.SignUpEmail.text.toString(), binding.SignUpPass.text.toString())
                    .addOnCompleteListener{task->
                        if (task.isSuccessful){
                            Toast.makeText(requireActivity(), "Account Created, please log in again", Toast.LENGTH_SHORT).show()

                            val user = auth.currentUser
                            user!!.updateProfile(profile)

                            db.collection("users").document(user.uid).set(userData).addOnSuccessListener {
                                auth.signOut()
                                Navigation.findNavController(view).popBackStack()
                            }


                        }
                    }
            }
        }
    }
}