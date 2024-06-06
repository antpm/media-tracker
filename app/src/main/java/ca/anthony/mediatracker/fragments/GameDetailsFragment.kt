package ca.anthony.mediatracker.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import ca.anthony.mediatracker.R
import ca.anthony.mediatracker.models.Game
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.storage.storage

class GameDetailsFragment : Fragment() {

    private var game = Game()
    private lateinit var closeButton: ImageButton
    private lateinit var navBar: BottomNavigationView

    private val storage = Firebase.storage.reference.child("images/games")

    //details
    private lateinit var image: ImageView
    private lateinit var title: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //disable the navbar when the fragment loads
        navBar = requireActivity().findViewById(R.id.BottomNav)
        navBar.visibility = View.GONE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        closeButton = requireActivity().findViewById(R.id.GameDetailsCloseButton)
        closeButton.setOnClickListener {
            //closes the fragment and re-enables the navbar
            requireActivity().supportFragmentManager.popBackStack()
            navBar.visibility = View.VISIBLE
        }
        image = requireActivity().findViewById(R.id.GameDetailImage)
        title = requireActivity().findViewById(R.id.GameDetailTitle)
        setDetails()
    }

    fun setGame(newGame: Game){
        //sets the game that is being viewed, called from the game recyclerview's adapter
        game = newGame
    }

    private fun setDetails(){
        //get a reference to the image, then get download url and use Glide to put image in imageView
        val imageRef = storage.child(game.image.toString())
        imageRef.downloadUrl.addOnSuccessListener{
            Glide.with(requireActivity()).load(it).into(image)
        }
        //set the rest of the details
        title.text = game.title
    }

}