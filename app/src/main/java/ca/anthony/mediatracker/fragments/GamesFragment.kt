package ca.anthony.mediatracker.fragments

import android.os.Bundle
import android.telecom.QueryLocationException
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
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore


class GamesFragment : Fragment() {

    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth

    private var _binding: FragmentGamesBinding? = null
    private val binding get() = _binding!!

    private var gameList: ArrayList<Game> = arrayListOf()
    private var gameIDList: ArrayList<String> = arrayListOf()
    private var listMode: Int = 1
    private var gameAdapter = GameAdapter(gameList, gameIDList, listMode)


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

        binding.GameAddButton.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_game_fragment_to_game_add_fragment)
        }

        binding.GameRecycler.layoutManager = LinearLayoutManager(context)
        binding.GameRecycler.adapter = gameAdapter

        binding.GameSortCompleteButton.setOnClickListener {
            if (listMode != 1) {
                listMode = 1
                sortGames()
            }
        }


        binding.GameSortRatingButton.setOnClickListener {
            if (listMode != 2) {
                listMode = 2
                sortGames()
            }
        }

        loadGames()
    }

    private fun loadGames(){
        gameList.clear()
        val data = db.collection("users").document(auth.currentUser!!.uid).collection("games").orderBy("complete", Query.Direction.DESCENDING).get()

        data.addOnSuccessListener {docs ->
            for (doc in docs){
                val game = doc.toObject(Game::class.java)
                gameIDList.add(doc.id)
                gameList.add(game)

            }
            gameAdapter.notifyDataSetChanged()
            binding.GameRecycler.scheduleLayoutAnimation()
        }.addOnFailureListener { exception->
            Log.e("Firestore error", exception.message.toString())
        }
    }

    private fun sortGames(){
        when (listMode){
            1-> gameList.sortByDescending { it.complete }
            2-> gameList.sortByDescending { it.rating }
        }

        gameAdapter.changeMode(listMode)
        gameAdapter.notifyDataSetChanged()
        binding.GameRecycler.scheduleLayoutAnimation()
    }
}