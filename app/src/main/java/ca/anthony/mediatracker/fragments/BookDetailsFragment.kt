package ca.anthony.mediatracker.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.Navigation
import ca.anthony.mediatracker.R
import ca.anthony.mediatracker.databinding.FragmentBookDetailsBinding
import ca.anthony.mediatracker.databinding.FragmentGameDetailsBinding
import ca.anthony.mediatracker.models.Book
import ca.anthony.mediatracker.models.Game
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import java.text.SimpleDateFormat
import java.util.Locale


class BookDetailsFragment : Fragment() {

    private var _binding: FragmentBookDetailsBinding? = null
    private val binding get() = _binding!!

    private var book = Book()
    private var id = ""
    private lateinit var navBar: BottomNavigationView

    private val storage = Firebase.storage.reference.child("images/books")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultListener("book edited"){key, bundle ->
            book = bundle.getSerializable("book") as Book
            id = bundle.getString("id") as String
            setDetails()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBookDetailsBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        book = arguments?.getSerializable("book") as Book
        id = arguments?.getString("id") as String
        binding.BookDetailCloseButton.setOnClickListener {

            Navigation.findNavController(view).popBackStack()
        }


        binding.BookDetailEditButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("book", book)
            bundle.putString("id", id)
            Navigation.findNavController(it).navigate(R.id.action_book_details_fragment_to_book_add_fragment, bundle)
        }

        setDetails()
    }

    private fun setDetails(){
        //get a reference to the image, then get download url and use Glide to put image in imageView
        val imageRef = storage.child(book.image.toString())
        imageRef.downloadUrl.addOnSuccessListener{
            Glide.with(requireActivity()).load(it).into(binding.BookDetailImage)
        }

        //set rating image
        when (book.rating) {
            1-> binding.BookDetailRating.setImageResource(R.drawable.star1)
            2-> binding.BookDetailRating.setImageResource(R.drawable.star2)
            3-> binding.BookDetailRating.setImageResource(R.drawable.star3)
            4-> binding.BookDetailRating.setImageResource(R.drawable.star4)
            5-> binding.BookDetailRating.setImageResource(R.drawable.star5)
        }

        //set title
        binding.BookDetailTitle.text = book.title

        //set release date and complete date
        val format = SimpleDateFormat("MMM dd, yyyy", Locale.US)

        binding.BookDetailReleaseDate.text = requireActivity().getString(R.string.release_date, format.format(book.release!!))
        binding.BookDetailCompleteDate.text = requireActivity().getString(R.string.complete_date, format.format(book.complete!!))

        //set dev
        binding.BookDetailAuthor.text = book.author

        //set pub
        binding.BookDetailPublisher.text =requireActivity().getString(R.string.publisher, book.publisher)

        //set genre
        binding.BookDetailGenre.text = requireActivity().getString(R.string.genre, book.genre)
    }


}