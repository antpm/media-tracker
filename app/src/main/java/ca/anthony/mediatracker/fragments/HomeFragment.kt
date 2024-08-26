package ca.anthony.mediatracker.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import ca.anthony.mediatracker.R
import ca.anthony.mediatracker.adapters.HomeBookAdapter
import ca.anthony.mediatracker.adapters.HomeGameAdapter
import ca.anthony.mediatracker.databinding.FragmentHomeBinding
import ca.anthony.mediatracker.models.Book
import ca.anthony.mediatracker.models.Game
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage

@Suppress("DEPRECATION")
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    private lateinit var navBar: BottomNavigationView


    private val db = Firebase.firestore
    private val gameStorage = Firebase.storage.reference.child("images/games")
    private val bookStorage = Firebase.storage.reference.child("images/books")

    //game variables
    private var game: Game = Game()
    private var book: Book = Book()
    private var gameImage: Uri = Uri.EMPTY
    private var bookImage: Uri = Uri.EMPTY
    private lateinit var gameAdapter: HomeGameAdapter
    private lateinit var bookAdapter: HomeBookAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        navBar = requireActivity().findViewById(R.id.BottomNav)
        navBar.visibility = View.VISIBLE
        return view

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        val user = auth.currentUser

        binding.HomeWelcome.text = requireActivity().getString(R.string.home_welcome, user!!.displayName)

        binding.HomeToolbar.inflateMenu(R.menu.menu_home)
        binding.HomeToolbar.setOnMenuItemClickListener {
            when (it.itemId){
                R.id.log_out -> {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
                    builder
                        .setTitle("Do you want to log out?")
                        .setPositiveButton("Confirm"){dialog, which ->
                            auth.signOut()
                            navBar.visibility = View.GONE
                            Toast.makeText(requireActivity(), "Logged Out", Toast.LENGTH_SHORT).show()
                            Navigation.findNavController(view).navigate(R.id.action_home_fragment_to_log_in)
                        }
                        .setNegativeButton("Cancel"){ _, _ -> }
                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                    true
                }
                R.id.account ->{
                    navBar.visibility = View.GONE
                    Navigation.findNavController(view).navigate(R.id.action_home_fragment_to_account_fragment)
                    true
                }
                else -> {
                    super.onOptionsItemSelected(it)
                }
            }
        }


        getLatestGame()
        getLatestBook()

    }

    private fun getLatestGame(){
        var id = ""
        val data = db.collection("users").document(auth.currentUser!!.uid).collection("games").orderBy("complete", Query.Direction.DESCENDING).limit(1).get()
        data.addOnSuccessListener {docs ->
            for (doc in docs){
                game = doc.toObject(Game::class.java)
                id = doc.id
            }

            if (game.title != null){
                val imageRef = gameStorage.child(game.image.toString())
                imageRef.downloadUrl.addOnSuccessListener{
                    gameImage = it

                    gameAdapter = HomeGameAdapter(game, gameImage, id)
                    binding.HomeGameRecycler.layoutManager = LinearLayoutManager(context)
                    binding.HomeGameRecycler.adapter = gameAdapter
                    binding.HomeGameRecycler.scheduleLayoutAnimation()
                    binding.HomeGameLabel.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun getLatestBook(){
        var id = ""
        val data = db.collection("users").document(auth.currentUser!!.uid).collection("books").orderBy("complete", Query.Direction.DESCENDING).limit(1).get()
        data.addOnSuccessListener {docs ->
            for (doc in docs){
                book = doc.toObject(Book::class.java)
                id = doc.id
            }

            if (book.title != null){
                val imageRef = bookStorage.child(book.image.toString())
                imageRef.downloadUrl.addOnSuccessListener{
                    bookImage = it

                    bookAdapter = HomeBookAdapter(book, bookImage, id)
                    binding.HomeBookRecycler.layoutManager = LinearLayoutManager(context)
                    binding.HomeBookRecycler.adapter = bookAdapter
                    binding.HomeBookRecycler.scheduleLayoutAnimation()
                    binding.HomeBookLabel.visibility = View.VISIBLE
                }
            }
        }
    }
}