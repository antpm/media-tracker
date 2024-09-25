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
        holder.bookGenre.text = book.genre

        Glide.with(context).load(image).into(holder.bookImage)

        val format = SimpleDateFormat("MMMM dd, yyyy", Locale.US)
        holder.bookComplete.text = format.format(book.complete)

        when (book.rating) {
            1-> holder.bookRating.setImageResource(R.drawable.star1)
            2-> holder.bookRating.setImageResource(R.drawable.star2)
            3-> holder.bookRating.setImageResource(R.drawable.star3)
            4-> holder.bookRating.setImageResource(R.drawable.star4)
            5-> holder.bookRating.setImageResource(R.drawable.star5)
        }
    }

    override fun getItemCount(): Int {
        return 1
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val bookImage: ImageView = itemView.findViewById(R.id.HomeBookImage)
        val bookTitle: TextView = itemView.findViewById(R.id.HomeBookTitle)
        val bookAuthor: TextView = itemView.findViewById(R.id.HomeBookAuthor)
        val bookGenre: TextView = itemView.findViewById(R.id.HomeBookGenre)
        val bookRating: ImageView = itemView.findViewById(R.id.HomeBookRating)
        val bookComplete: TextView = itemView.findViewById(R.id.HomeBookCompleteDate)
    }

}