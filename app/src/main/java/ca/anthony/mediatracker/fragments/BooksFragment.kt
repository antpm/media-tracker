package ca.anthony.mediatracker.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import ca.anthony.mediatracker.R
import ca.anthony.mediatracker.adapters.BookAdapter
import ca.anthony.mediatracker.databinding.FragmentBooksBinding
import ca.anthony.mediatracker.models.Book
import ca.anthony.mediatracker.models.Game
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore


class BooksFragment : Fragment() {
    private var _binding: FragmentBooksBinding? = null
    private val binding get() = _binding!!

    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth

    private var bookList: ArrayList<Book> = arrayListOf()
    private var bookIDList: ArrayList<String> = arrayListOf()
    private var bookAdapter = BookAdapter(bookList, bookIDList)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBooksBinding.inflate(inflater,container,false)
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

        binding.BookSortSubCard.visibility = View.GONE
        binding.BookSortOptionsOpen.setOnClickListener {
            binding.BookSortSubCard.visibility = View.VISIBLE
            binding.BookSortOptionsOpen.visibility = View.INVISIBLE
            binding.BookSortOptionsClose.visibility = View.VISIBLE
        }

        binding.BookSortOptionsClose.setOnClickListener {
            binding.BookSortSubCard.visibility = View.GONE
            binding.BookSortOptionsOpen.visibility = View.VISIBLE
            binding.BookSortOptionsClose.visibility = View.INVISIBLE
        }

        binding.BooksRecycler.layoutManager = LinearLayoutManager(context)
        binding.BooksRecycler.adapter = bookAdapter

        binding.BooksAddButton.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_books_fragment_to_book_add_fragment)
        }

        loadBooks()
    }

    private fun loadBooks(){
        bookList.clear()
        val data = db.collection("users").document(auth.currentUser!!.uid).collection("books").orderBy("complete").get()

        data.addOnSuccessListener {docs ->
            for (doc in docs){
                val book = doc.toObject(Book::class.java)
                bookIDList.add(doc.id)
                bookList.add(book)

            }
            bookAdapter.notifyDataSetChanged()
        }.addOnFailureListener { exception->
            Log.e("Firestore error", exception.message.toString())
        }
    }
}