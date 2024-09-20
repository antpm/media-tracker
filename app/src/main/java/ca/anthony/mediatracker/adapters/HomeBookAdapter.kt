package ca.anthony.mediatracker.adapters

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import ca.anthony.mediatracker.R
import ca.anthony.mediatracker.models.Book
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale

class HomeBookAdapter(private val book: Book, private val image: Uri, private val id: String): RecyclerView.Adapter<HomeBookAdapter.ViewHolder>(){

    //gets context for use in various methods
    private lateinit var context: Context
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_home_latest_book, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HomeBookAdapter.ViewHolder, position: Int) {
        holder.bookTitle.text = book.title
        holder.bookAuthor.text = book.author

        Glide.with(context).load(image).into(holder.bookImage)

        val format = SimpleDateFormat("MMM dd, yyyy", Locale.US)
        holder.bookComplete.text = context.getString(R.string.complete_date, format.format(book.complete!!))

        holder.bookRating.text = context.getString(R.string.rating, book.rating)

        holder.bookDetailsButton.setOnClickListener{
            val bundle = Bundle()
            bundle.putSerializable("book", book)
            bundle.putString("id", id)
            Navigation.findNavController(holder.itemView).navigate(R.id.action_home_fragment_to_book_details_fragment, bundle)
        }
    }

    override fun getItemCount(): Int {
        return 1
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val bookImage: ImageView = itemView.findViewById(R.id.HomeBookImage)
        val bookTitle: TextView = itemView.findViewById(R.id.HomeBookTitle)
        val bookAuthor: TextView = itemView.findViewById(R.id.HomeBookAuthor)
        val bookRating: TextView = itemView.findViewById(R.id.HomeBookRating)
        val bookComplete: TextView = itemView.findViewById(R.id.HomeBookCompleteDate)
        val bookDetailsButton: Button = itemView.findViewById(R.id.HomeBookDetailsButton)
    }

}