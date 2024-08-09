package ca.anthony.mediatracker.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.anthony.mediatracker.R
import ca.anthony.mediatracker.adapters.HomeGameAdapter
import ca.anthony.mediatracker.databinding.FragmentHomeBinding
import ca.anthony.mediatracker.models.Game
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val db = Firebase.firestore
    private val gameStorage = Firebase.storage.reference.child("images/games")

    //game variables
    private var game: Game = Game()
    private var image: Uri = Uri.EMPTY
    private lateinit var gameAdapter: HomeGameAdapter
    //private lateinit var gameRecycler: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //gameRecycler = view.findViewById(R.id.HomeGameRecycler)

        getLatestGame()

    }

    private fun getLatestGame(){
        var id = ""
        val data = db.collection("games").orderBy("complete", Query.Direction.DESCENDING).limit(1).get()
        data.addOnSuccessListener {docs ->
            for (doc in docs){
                game = doc.toObject(Game::class.java)
                id = doc.id
            }

            if (game.title != null){
                val imageRef = gameStorage.child(game.image.toString())
                imageRef.downloadUrl.addOnSuccessListener{
                    image = it

                    gameAdapter = HomeGameAdapter(game, image, id)
                    binding.HomeGameRecycler.layoutManager = LinearLayoutManager(context)
                    binding.HomeGameRecycler.adapter = gameAdapter
                }
            }
        }
    }
}