package ca.anthony.mediatracker.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.anthony.mediatracker.R
import ca.anthony.mediatracker.adapters.GameAdapter
import ca.anthony.mediatracker.models.Game
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore


class GamesFragment : Fragment() {

    private val db = Firebase.firestore

    private var gameList: ArrayList<Game> = arrayListOf()
    private lateinit var gameRecycler: RecyclerView
    private var gameAdapter = GameAdapter(gameList)
    private lateinit var addButton: FloatingActionButton


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_games, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gameRecycler = view.findViewById(R.id.GameRecycler)
        gameRecycler.layoutManager = LinearLayoutManager(context)
        gameRecycler.adapter = gameAdapter
        addButton = view.findViewById(R.id.GameAddButton)

        addButton.setOnClickListener {
            //val addFragment = GameAddFragment()
            //val transaction = requireActivity().supportFragmentManager.beginTransaction()
            //transaction.replace(R.id.NavHost, addFragment)
            //transaction.addToBackStack(null)
            //transaction.commit()
            Navigation.findNavController(view).navigate(R.id.action_game_fragment_to_game_add_fragment)
        }

        loadGames()
    }

    private fun loadGames(){
        gameList.clear()
        val data = db.collection("games").orderBy("complete").get()

        data.addOnSuccessListener {docs ->
            for (doc in docs){
                val game = doc.toObject(Game::class.java)
                gameList.add(game)
            }
            gameAdapter.notifyDataSetChanged()
        }.addOnFailureListener { exception->
            Log.e("Firestore error", exception.message.toString())
        }
    }
}