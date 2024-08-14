package ca.anthony.mediatracker.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.setFragmentResult
import androidx.navigation.Navigation
import ca.anthony.mediatracker.R
import ca.anthony.mediatracker.databinding.FragmentBookAddBinding
import ca.anthony.mediatracker.databinding.FragmentGameAddBinding
import ca.anthony.mediatracker.models.Book
import ca.anthony.mediatracker.models.Game
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.Date
import java.util.Locale


class BookAddFragment : Fragment() {

    private var _binding: FragmentBookAddBinding? = null
    private val binding get() = _binding!!

    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    private val storage =  Firebase.storage.reference.child("images/books")
    private var editBook = Book()
    private var editID = ""
    private var oldImage = ""

    //values
    private var releaseDate: Long = 0
    private var completeDate:Long = 0
    private var image: Uri = Uri.EMPTY
    private var editing = false

    //launcher for selecting an image
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()){
        val fileName = getFileNameFromUri(requireActivity(), it!!)
        image = it
        binding.BookAddImageName.text = image.lastPathSegment
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBookAddBinding.inflate(inflater,container,false)
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

        if (arguments != null){
            editBook = arguments?.getSerializable("book") as Book
            editID = arguments?.getString("id") as String
            //enableEditing(editBook)
        }

        binding.BookAddReleaseDate.setOnClickListener {
            showDatePicker(binding.BookAddReleaseDate)
        }

        binding.BookAddCompDate.setOnClickListener {
            showDatePicker(binding.BookAddCompDate)
        }

        binding.BookAddImageButton.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        binding.BookAddSaveButton.setOnClickListener {
            validateInput(it)
        }

        binding.BookAddCancelButton.setOnClickListener {
            Navigation.findNavController(view).popBackStack()
        }
    }

    private fun showDatePicker(textField: EditText){
        val datePicker = MaterialDatePicker.Builder.datePicker().build()
        datePicker.show(parentFragmentManager, "DatePicker")

        datePicker.addOnPositiveButtonClickListener{
            //convert selected date to local timezone to fix date being off by one
            val utcDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneOffset.UTC)
            val myTimeZoneDate = ZonedDateTime.of(utcDate, ZoneId.systemDefault())

            //format date and insert into text field
            val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.US)
            val date = dateFormatter.format(Date(myTimeZoneDate.toInstant().toEpochMilli()))

            //set values based on textfield that was clicked
            if (textField == binding.BookAddReleaseDate){
                releaseDate = myTimeZoneDate.toInstant().toEpochMilli()
                binding.BookAddReleaseDate.setText(date)
            } else if (textField == binding.BookAddCompDate){
                completeDate = myTimeZoneDate.toInstant().toEpochMilli()
                binding.BookAddCompDate.setText(date)
            }
        }
    }

    private fun saveBook(view: View){
        var imageName = "noimage.jpg"
        if (editing) imageName = binding.BookAddImageName.text.toString()
        if (image != Uri.EMPTY ) imageName = image.lastPathSegment.toString()
        val book = Book(binding.BookAddTitle.text.toString(), binding.BookAddAuthor.text.toString(), binding.BookAddGenre.text.toString(), binding.BookAddPublisher.text.toString(), binding.BookAddRating.text.toString().toInt(), Date(releaseDate), Date(completeDate), imageName)

        //if editing, set over existing game
        if (editing){
            val gameRef = db.collection("users").document(auth.currentUser!!.uid).collection("books").document(editID).set(book).addOnSuccessListener {
                Toast.makeText(context, "Book Updated", Toast.LENGTH_LONG).show()
                //check if the image has been updated, if so upload new image and delete old image
                if (book.image != oldImage && image != Uri.EMPTY){
                    val imageRef = storage.child("${image.lastPathSegment}")
                    imageRef.child(oldImage).delete()
                    imageRef.putFile(image)
                }
                val bundle = Bundle()
                bundle.putSerializable("book", book)
                bundle.putString("id", editID)
                setFragmentResult("book edited", bundle)
                Navigation.findNavController(view).popBackStack()

            }.addOnFailureListener {
                Toast.makeText(context, "Book Not Updated", Toast.LENGTH_SHORT).show()
            }
        } else {
            db.collection("users").document(auth.currentUser!!.uid).collection("books").add(book).addOnSuccessListener {
                Toast.makeText(context, "Book Added", Toast.LENGTH_LONG).show()
                if (image != Uri.EMPTY){
                    val imageRef = storage.child("${image.lastPathSegment}")
                    imageRef.putFile(image)
                }
                Navigation.findNavController(view).popBackStack()
            }.addOnFailureListener {
                Toast.makeText(context, "Book Not Added", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("Range")
    private fun getFileNameFromUri(context: Context, uri: Uri): String? {
        val fileName: String?
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.moveToFirst()
        fileName = cursor?.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        cursor?.close()
        return fileName
    }

    private fun validateInput(view: View){
        //maybe add more validation checking later
        if (!checkBlank()){
            if (binding.BookAddRating.text.toString().toInt() > 5 || binding.BookAddRating.text.toString().toInt() <= 0){
                Toast.makeText(requireActivity(), "Rating must be a number between 1 and 5", Toast.LENGTH_LONG).show()
            } else {
                saveBook(view)

            }
        } else {
            Toast.makeText(requireActivity(), "All fields must be filled out", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkBlank(): Boolean{
        return (binding.BookAddTitle.text.isEmpty() || binding.BookAddAuthor.text.isEmpty() || binding.BookAddPublisher.text.isEmpty() || binding.BookAddGenre.text.isEmpty() || binding.BookAddRating.text.isEmpty() || binding.BookAddReleaseDate.text.isEmpty() || binding.BookAddCompDate.text.isEmpty())

    }

}