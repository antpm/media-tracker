package ca.anthony.mediatracker.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ca.anthony.mediatracker.R
import ca.anthony.mediatracker.models.Book
import java.text.SimpleDateFormat
import java.util.Locale

class BookAdapter(private val bookList: ArrayList<Book>, private val bookIDList: ArrayList<String>):RecyclerView.Adapter<BookAdapter.ViewHolder>() {

    //gets context for use in various methods
    private var context: Context? = null
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_book_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookAdapter.ViewHolder, position: Int) {
        val book = bookList[position]
        val id = bookIDList[position]

        val format = SimpleDateFormat("MMM dd, yyyy", Locale.US)

        holder.bookTitle.text = book.title
        holder.bookAuthor.text = book.author
        holder.bookComplete.text = context?.getString(R.string.complete_date, format.format(book.complete!!))
        holder.bookRating.text = context?.getString(R.string.rating, book.rating)

        holder.bookButton.setOnClickListener {
            //TODO: setup detail button after detail screen is made
        }
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val bookTitle:TextView = itemView.findViewById(R.id.BookTitle)
        val bookAuthor: TextView = itemView.findViewById(R.id.BookAuthor)
        val bookComplete: TextView = itemView.findViewById(R.id.BookComplete)
        val bookRating: TextView = itemView.findViewById(R.id.BookRating)
        val bookButton: Button = itemView.findViewById(R.id.BookListDetailButton)
    }

}