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
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.Navigation
import ca.anthony.mediatracker.R
import ca.anthony.mediatracker.models.Game
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import java.io.File
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.Date
import java.util.Locale

class GameAddFragment : Fragment() {

    private val db = Firebase.firestore
    private val storage =  Firebase.storage.reference.child("images/games")

    //fields
    private lateinit var title: EditText
    private lateinit var developer: EditText
    private lateinit var publisher: EditText
    private lateinit var platform: EditText
    private lateinit var genre: EditText
    private lateinit var rating: EditText
    private lateinit var releaseDateTxt: EditText
    private lateinit var completeDateTxt: EditText
    private lateinit var imageText: TextView

    private lateinit var header: TextView

    //buttons
    private lateinit var imageButton: Button
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button

    //values
    private var releaseDate: Long = 0
    private var completeDate:Long = 0
    private var image: Uri = Uri.EMPTY
    private var editing = false

    //launcher for selecting an image
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()){
        val fileName = getFileNameFromUri(requireActivity(), it!!)
        image = it
        imageText.text = image.lastPathSegment
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        title = view.findViewById(R.id.GameAddTitle)
        developer = view.findViewById(R.id.GameAddDev)
        publisher = view.findViewById(R.id.GameAddPublisher)
        platform = view.findViewById(R.id.GameAddPlatform)
        genre = view.findViewById(R.id.GameAddGenre)
        rating = view.findViewById(R.id.GameAddRating)
        releaseDateTxt = view.findViewById(R.id.GameAddReleaseDate)
        completeDateTxt = view.findViewById(R.id.GameAddCompDate)
        imageText = view.findViewById(R.id.GameAddImageName)

        header = view.findViewById(R.id.GameAddLabel)

        imageButton = view.findViewById(R.id.GameAddImageButton)
        saveButton = view.findViewById(R.id.GameAddSaveButton)
        cancelButton = view.findViewById(R.id.GameAddCancelButton)


        val editGame = arguments?.getSerializable("game") as Game
        if (editGame.title != null){
            enableEditing(editGame)
        }

        releaseDateTxt.setOnClickListener {
            showDatePicker(releaseDateTxt)
        }

        completeDateTxt.setOnClickListener {
            showDatePicker(completeDateTxt)
        }

        imageButton.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        saveButton.setOnClickListener {
            validateInput(it)
        }

        cancelButton.setOnClickListener {
            Navigation.findNavController(view).popBackStack()
        }
    }

    private fun enableEditing(editGame: Game) {
        //set editing true
        editing = true

        val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.US)

        //change header
        header.text = context?.getString(R.string.game_edit_label)

        //set values in fields
        title.setText(editGame.title)
        developer.setText(editGame.developer)
        publisher.setText(editGame.publisher)
        platform.setText(editGame.platform)
        genre.setText(editGame.genre)
        rating.setText(editGame.rating.toString())
        val rDate = dateFormatter.format(Date(editGame.release!!.toInstant().toEpochMilli()))
        releaseDateTxt.setText(rDate)
        val cDate = dateFormatter.format(Date(editGame.complete!!.toInstant().toEpochMilli()))
        completeDateTxt.setText(cDate)
        imageText.text = editGame.image

    }

    private fun showDatePicker(textField:EditText){
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
            if (textField == releaseDateTxt){
                releaseDate = myTimeZoneDate.toInstant().toEpochMilli()
                releaseDateTxt.setText(date)
            } else if (textField == completeDateTxt){
                completeDate = myTimeZoneDate.toInstant().toEpochMilli()
                completeDateTxt.setText(date)
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

    private fun saveGame(view: View){
        var imageName = "noimage.jpg"
        if (image != Uri.EMPTY ) imageName = image.lastPathSegment.toString()
        val game = Game(title.text.toString(), developer.text.toString(), publisher.text.toString(),platform.text.toString(), genre.text.toString(), rating.text.toString().toInt(), Date(releaseDate), Date(completeDate), imageName)
        db.collection("games").add(game).addOnSuccessListener {

            Toast.makeText(context, "Game Added", Toast.LENGTH_LONG).show()
            if (image != Uri.EMPTY){
                val imageRef = storage.child("${image.lastPathSegment}")
                imageRef.putFile(image)
            }
            Navigation.findNavController(view).popBackStack()

        }.addOnFailureListener {
            Toast.makeText(context, "Game Not Added", Toast.LENGTH_SHORT).show()
        }


    }

    private fun validateInput(view: View){
        //maybe add more validation checking later
        if (!checkBlank()){
            if (rating.text.toString().toInt() > 5 || rating.text.toString().toInt() <= 0){
                Toast.makeText(requireActivity(), "Rating must be a number between 1 and 5", Toast.LENGTH_LONG).show()
            } else {
                saveGame(view)

            }
        } else {
            Toast.makeText(requireActivity(), "All fields must be filled out", Toast.LENGTH_SHORT).show()
        }

    }

    private fun checkBlank(): Boolean{
        return (title.text.isEmpty() || developer.text.isEmpty() || publisher.text.isEmpty() || platform.text.isEmpty() || genre.text.isEmpty() || rating.text.isEmpty() || releaseDateTxt.text.isEmpty() || completeDateTxt.text.isEmpty())

    }

}