package ca.anthony.mediatracker.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.anthony.mediatracker.R
import ca.anthony.mediatracker.adapters.GameAdapter
import ca.anthony.mediatracker.databinding.FragmentGamesBinding
import ca.anthony.mediatracker.models.Game
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore


class GamesFragment : Fragment() {

    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth

    private var _binding: FragmentGamesBinding? = null
    private val binding get() = _binding!!

    private var gameList: ArrayList<Game> = arrayListOf()
    private var gameIDList: ArrayList<String> = arrayListOf()
    private var gameAdapter = GameAdapter(gameList, gameIDList)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentGamesBinding.inflate(inflater,container, false)
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


        //binding.GameTestText.visibility = View.GONE
        binding.GameSortSubCard.visibility = View.GONE
        binding.GameSortOptionsOpen.setOnClickListener {
            //binding.GameTestText.visibility = View.VISIBLE
            binding.GameSortSubCard.visibility = View.VISIBLE
            binding.GameSortOptionsOpen.visibility = View.INVISIBLE
            binding.GameSortOptionsClose.visibility = View.VISIBLE
        }

        binding.GameSortOptionsClose.setOnClickListener {
            //binding.GameTestText.visibility = View.GONE
            binding.GameSortSubCard.visibility = View.GONE
            binding.GameSortOptionsOpen.visibility = View.VISIBLE
            binding.GameSortOptionsClose.visibility = View.INVISIBLE
        }


        binding.GameRecycler.layoutManager = LinearLayoutManager(context)
        binding.GameRecycler.adapter = gameAdapter


        binding.GameAddButton.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_game_fragment_to_game_add_fragment)
        }

        loadGames()
    }

    private fun loadGames(){
        gameList.clear()
        val data = db.collection("users").document(auth.currentUser!!.uid).collection("games").orderBy("complete").get()

        data.addOnSuccessListener {docs ->
            for (doc in docs){
                val game = doc.toObject(Game::class.java)
                gameIDList.add(doc.id)
                gameList.add(game)

            }
            gameAdapter.notifyDataSetChanged()
        }.addOnFailureListener { exception->
            Log.e("Firestore error", exception.message.toString())
        }
    }
}