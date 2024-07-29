package ca.anthony.mediatracker.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.navigation.Navigator
import androidx.navigation.findNavController
import ca.anthony.mediatracker.R
import ca.anthony.mediatracker.models.Game
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import java.text.SimpleDateFormat
import java.util.Locale

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
    private lateinit var genre: TextView
    private lateinit var platform: TextView
    private lateinit var completeDate: TextView
    private lateinit var releaseDate: TextView

    private lateinit var editButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //disable the navbar when the fragment loads
        navBar = requireActivity().findViewById(R.id.BottomNav)
        //navBar.visibility = View.GONE
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
        game = arguments?.getSerializable("game") as Game
        closeButton = requireActivity().findViewById(R.id.GameDetailCloseButton)
        closeButton.setOnClickListener {
            //closes the fragment and re-enables the navbar
            //requireActivity().findNavController(view).popBackStack()
            Navigation.findNavController(view).popBackStack()
        }
        image = view.findViewById(R.id.GameDetailImage)
        title = view.findViewById(R.id.GameDetailTitle)
        rating = view.findViewById(R.id.GameDetailRating)
        developer = view.findViewById(R.id.GameDetailDeveloper)
        publisher = view.findViewById(R.id.GameDetailPublisher)
        genre = view.findViewById(R.id.GameDetailGenre)
        platform = view.findViewById(R.id.GameDetailPlatform)
        completeDate = view.findViewById(R.id.GameDetailCompleteDate)
        releaseDate = view.findViewById(R.id.GameDetailReleaseDate)
        editButton = view.findViewById(R.id.GameDetailEditButton)

        editButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("game", game)
            Navigation.findNavController(it).navigate(R.id.action_game_detail_fragment_to_game_add_fragment, bundle)
        }

        setDetails()
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

        //set release date and complete date
        val format = SimpleDateFormat("MMM dd, yyyy", Locale.US)

        releaseDate.text = requireActivity().getString(R.string.game_release_date, format.format(game.release!!))
        completeDate.text = requireActivity().getString(R.string.game_complete_date, format.format(game.complete!!))

        //set dev
        developer.text = requireActivity().getString(R.string.game_developer, game.developer)

        //set pub
        publisher.text =requireActivity().getString(R.string.game_publisher, game.publisher)

        //set platform
        platform.text = requireActivity().getString(R.string.game_platform, game.platform)

        //set genre
        genre.text = requireActivity().getString(R.string.game_genre, game.genre)
    }

}