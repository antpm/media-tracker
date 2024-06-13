package ca.anthony.mediatracker.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
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
    private lateinit var rating: ImageView
    private lateinit var developer: TextView
    private lateinit var publisher: TextView

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
        rating = requireActivity().findViewById(R.id.GameDetailRating)
        developer = requireActivity().findViewById(R.id.GameDetailDeveloper)
        publisher = requireActivity().findViewById(R.id.GameDetailPublisher)

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

        //set rating image
        when (game.rating) {
            1-> rating.setImageResource(R.drawable.star1)
            2-> rating.setImageResource(R.drawable.star2)
            3-> rating.setImageResource(R.drawable.star3)
            4-> rating.setImageResource(R.drawable.star4)
            5-> rating.setImageResource(R.drawable.star5)
        }

        //set title
        title.text = game.title

        //set release date

        //set complete date

        //set dev
        developer.text = requireActivity().getString(R.string.game_developer, game.developer)

        //set pub
        publisher.text =requireActivity().getString(R.string.game_publisher, game.publisher)

        //set platform

        //set genre
    }

}