package ca.anthony.mediatracker.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.Navigation
import ca.anthony.mediatracker.R
import ca.anthony.mediatracker.databinding.FragmentGameDetailsBinding
import ca.anthony.mediatracker.models.Game
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import java.text.SimpleDateFormat
import java.util.Locale

class GameDetailsFragment : Fragment() {

    private var _binding: FragmentGameDetailsBinding? = null
    private val binding get() = _binding!!

    private var game = Game()
    private var id = ""
    private lateinit var navBar: BottomNavigationView

    private val storage = Firebase.storage.reference.child("images/games")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultListener("game edited"){key, bundle ->
            game = bundle.getSerializable("game") as Game
            id = bundle.getString("id") as String
            setDetails()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentGameDetailsBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        game = arguments?.getSerializable("game") as Game
        id = arguments?.getString("id") as String
        binding.GameDetailCloseButton.setOnClickListener {

            Navigation.findNavController(view).popBackStack()
        }


        binding.GameDetailEditButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("game", game)
            bundle.putString("id", id)
            Navigation.findNavController(it).navigate(R.id.action_game_detail_fragment_to_game_add_fragment, bundle)
        }

        setDetails()
    }

    private fun setDetails(){
        //get a reference to the image, then get download url and use Glide to put image in imageView
        val imageRef = storage.child(game.image.toString())
        imageRef.downloadUrl.addOnSuccessListener{
            Glide.with(requireActivity()).load(it).into(binding.GameDetailImage)
        }

        //set rating image
        when (game.rating) {
            1-> binding.GameDetailRating.setImageResource(R.drawable.star1)
            2-> binding.GameDetailRating.setImageResource(R.drawable.star2)
            3-> binding.GameDetailRating.setImageResource(R.drawable.star3)
            4-> binding.GameDetailRating.setImageResource(R.drawable.star4)
            5-> binding.GameDetailRating.setImageResource(R.drawable.star5)
        }

        //set title
        binding.GameDetailTitle.text = game.title

        //set release date and complete date
        val format = SimpleDateFormat("MMM dd, yyyy", Locale.US)

        binding.GameDetailReleaseDate.text = requireActivity().getString(R.string.release_date, format.format(game.release!!))
        binding.GameDetailCompleteDate.text = requireActivity().getString(R.string.complete_date, format.format(game.complete!!))

        //set dev
        binding.GameDetailDeveloper.text = requireActivity().getString(R.string.developer, game.developer)

        //set pub
        binding.GameDetailPublisher.text =requireActivity().getString(R.string.publisher, game.publisher)

        //set platform
        binding.GameDetailPlatform.text = requireActivity().getString(R.string.platform, game.platform)

        //set genre
        binding.GameDetailGenre.text = requireActivity().getString(R.string.genre, game.genre)
    }

}