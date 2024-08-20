package ca.anthony.mediatracker.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import ca.anthony.mediatracker.R
import ca.anthony.mediatracker.databinding.FragmentAccountBinding
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAccountBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.AccountToolbar.setTitle("Account Management")
        binding.AccountToolbar.setNavigationIcon(R.drawable.back_arrow)
        binding.AccountToolbar.setNavigationOnClickListener {
            Navigation.findNavController(it).popBackStack()
        }
        
        

        auth = Firebase.auth
        val user: FirebaseUser = auth.currentUser!!

        binding.AccountName.setText(user.displayName)
        
        
        binding.AccountNameSave.setOnClickListener { 
            saveName(user)
        }


    }
    
    private fun saveName(user: FirebaseUser){
        if (binding.AccountName.text.isNotEmpty()){
            val profile = userProfileChangeRequest {
                displayName = binding.AccountName.text.toString()
            }

            user.updateProfile(profile).addOnCompleteListener { task->
                if (task.isSuccessful){
                    Toast.makeText(requireActivity(), "Display Name updated", Toast.LENGTH_SHORT).show()
                } 
            }
        } else {
            Toast.makeText(requireActivity(), "Display Name cannot be blank", Toast.LENGTH_SHORT).show()
        }
        
    }
}