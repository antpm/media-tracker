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

        binding.AccountNameSubcardShow.setOnClickListener {
            binding.AccountNameSubcard.visibility = View.VISIBLE
            binding.AccountNameSubcardHide.visibility = View.VISIBLE
            binding.AccountNameSubcardShow.visibility = View.INVISIBLE
        }

        binding.AccountNameSubcardHide.setOnClickListener {
            binding.AccountNameSubcard.visibility = View.GONE
            binding.AccountNameSubcardShow.visibility = View.VISIBLE
            binding.AccountNameSubcardHide.visibility = View.INVISIBLE
        }

        binding.AccountPassSubcardShow.setOnClickListener {
            binding.AccountPassSubcard.visibility = View.VISIBLE
            binding.AccountPassSubcardHide.visibility = View.VISIBLE
            binding.AccountPassSubcardShow.visibility = View.INVISIBLE
        }

        binding.AccountPassSubcardHide.setOnClickListener {
            binding.AccountPassSubcard.visibility = View.GONE
            binding.AccountPassSubcardShow.visibility = View.VISIBLE
            binding.AccountPassSubcardHide.visibility = View.INVISIBLE
        }
        

        auth = Firebase.auth
        val user: FirebaseUser = auth.currentUser!!

        binding.AccountName.setText(user.displayName)
        
        binding.AccountNameSave.setOnClickListener {
            if (binding.AccountName.text.isNotEmpty()){
                saveName(user)
            } else {
                Toast.makeText(requireActivity(), "Display Name cannot be blank", Toast.LENGTH_SHORT).show()
            }
        }

        binding.AccountPassSave.setOnClickListener {
            if (binding.AccountNewPass.text.isNotEmpty() && binding.AccountOldPass.text.isNotEmpty()){
                savePass(user)
            } else {
                Toast.makeText(requireActivity(), "Both password fields must be filled out", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun saveName(user: FirebaseUser){
        val profile = userProfileChangeRequest {
            displayName = binding.AccountName.text.toString()
        }

        user.updateProfile(profile).addOnCompleteListener { task->
            if (task.isSuccessful){
                Toast.makeText(requireActivity(), "Display Name updated", Toast.LENGTH_SHORT).show()
                binding.AccountNameSubcardHide.performClick()
            }
        }
    }

    private fun savePass(user: FirebaseUser){
        val credential = EmailAuthProvider.getCredential(user.email.toString(), binding.AccountOldPass.text.toString())
        user.reauthenticate(credential).addOnCompleteListener { authTask->
            if (authTask.isSuccessful){
                user.updatePassword(binding.AccountNewPass.text.toString()).addOnCompleteListener {passTask->
                    if (passTask.isSuccessful){
                        Toast.makeText(requireActivity(), "Password Changed", Toast.LENGTH_SHORT).show()
                        binding.AccountOldPass.text.clear()
                        binding.AccountNewPass.text.clear()
                        binding.AccountPassSubcardHide.performClick()
                    } else {
                        Toast.makeText(requireActivity(), "Password could not be changed", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireActivity(), "Email/Password Incorrect", Toast.LENGTH_SHORT).show()
            }
        }
    }
}