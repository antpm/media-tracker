package ca.anthony.mediatracker.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import ca.anthony.mediatracker.R
import ca.anthony.mediatracker.databinding.FragmentLogInBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth


class LogIn : Fragment() {

    private var _binding: FragmentLogInBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLogInBinding.inflate(inflater,container,false)
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

        //val navBar = view.findViewById<BottomNavigationView>(R.id.BottomNav)
        //navBar.visibility = View.GONE

        //check if the user is already logged in and navigate to home screen if so
        val currentUser = auth.currentUser
        if (currentUser != null) {
            Navigation.findNavController(view).navigate(R.id.action_log_in_to_home_fragment)
        }

        binding.LogInCreateAccount.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_log_in_to_sign_up)
        }

        binding.LogInButton.setOnClickListener {
            if (binding.LogInEmail.text.isEmpty() || binding.LogInPassword.text.isEmpty()){
                Toast.makeText(requireActivity(), "Please fill out all fields", Toast.LENGTH_SHORT).show()
            } else {
                auth.signInWithEmailAndPassword(binding.LogInEmail.text.toString(), binding.LogInPassword.text.toString())
                    .addOnCompleteListener { task->
                        if (task.isSuccessful){
                            Toast.makeText(requireActivity(), "Login Succesful", Toast.LENGTH_SHORT).show()
                            Navigation.findNavController(view).navigate(R.id.action_log_in_to_home_fragment)
                        } else {
                            Toast.makeText(requireActivity(), "Email/Password Incorrect", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
}