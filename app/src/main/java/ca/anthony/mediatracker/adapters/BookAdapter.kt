package ca.anthony.mediatracker.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import ca.anthony.mediatracker.R
import ca.anthony.mediatracker.fragments.BookDetailsFragment
import ca.anthony.mediatracker.models.Book
import java.text.SimpleDateFormat
import java.util.Locale

class BookAdapter(private val bookList: ArrayList<Book>, private var mode:Int):RecyclerView.Adapter<BookAdapter.ViewHolder>() {

    //gets context for use in various methods
    private var context: Context? = null

    private var onClickListener: OnClickListener? = null
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

        val format = SimpleDateFormat("MMM dd, yyyy", Locale.US)

        holder.bookTitle.text = book.title
        holder.bookText.text = context?.getString(R.string.complete_date, format.format(book.complete!!))
        when (mode){
            //mode 1: show completion date
            1 -> holder.bookText.text = context?.getString(R.string.complete_date, format.format(book.complete!!))
            //mode 3: show rating
            2 -> holder.bookText.text = context?.getString(R.string.rating, book.rating)
        }
        //holder.bookRating.text = context?.getString(R.string.rating, book.rating)

        val bundle = Bundle()
        bundle.putSerializable("book", book)

        holder.itemView.setOnClickListener {
            val dialog = BookDetailsFragment()
            dialog.arguments = bundle
            dialog.show((holder.itemView.context as AppCompatActivity).supportFragmentManager, "dialog")
        }

        holder.bookEditButton.setOnClickListener {

            Navigation.findNavController(it).navigate(R.id.action_books_fragment_to_book_add_fragment, bundle)
        }
    }

    // Set the click listener for the adapter
    fun setOnClickListener(listener: OnClickListener?) {
        this.onClickListener = listener
    }

    // Interface for the click listener
    interface OnClickListener

    override fun getItemCount(): Int {
        return bookList.size
    }

    fun changeMode(newMode: Int){
        mode = newMode
    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val bookTitle:TextView = itemView.findViewById(R.id.BookTitle)
        val bookText: TextView = itemView.findViewById(R.id.BookText)
        val bookEditButton: ImageButton = itemView.findViewById(R.id.BookListEditButton)
    }

}