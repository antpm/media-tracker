package ca.anthony.mediatracker.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.setFragmentResult
import androidx.navigation.Navigation
import ca.anthony.mediatracker.R
import ca.anthony.mediatracker.databinding.FragmentGameAddBinding
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

class GameAddFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var _binding: FragmentGameAddBinding? = null
    private val binding get() = _binding!!

    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    private val storage =  Firebase.storage.reference.child("images/games")
    private var editGame = Game()
    private var editID = ""
    private var oldImage = ""

    //values
    private var completeDate:Long = 0
    private var fileName:String = "noimage.jpg"
    private var image: Uri = Uri.EMPTY
    private var editing = false
    private val ratingList: Array<Int> = arrayOf(1,2,3,4,5)

    //launcher for selecting an image
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()){
        if (it != null){

            fileName = getRandomString(30)
            image = it
            binding.GameAddImageName.visibility = View.VISIBLE
            binding.GameAddImageCheck.visibility = View.VISIBLE
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentGameAddBinding.inflate(inflater,container,false)
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

        binding.GameAddRating.onItemSelectedListener = this
        val ad: ArrayAdapter<*> = ArrayAdapter<Any?>(requireActivity(), android.R.layout.simple_spinner_item, ratingList)
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.GameAddRating.adapter = ad

        if (arguments != null){
            editGame = arguments?.getSerializable("game") as Game
            editID = editGame.id.toString()
            enableEditing(editGame)
        }



        binding.GameAddCompDate.setOnClickListener {
            showDatePicker(binding.GameAddCompDate)
        }

        binding.GameAddImageButton.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        binding.GameAddSaveButton.setOnClickListener {
            validateInput(it)
        }

        binding.GameAddCancelButton.setOnClickListener {
            Navigation.findNavController(view).popBackStack()
        }
    }

    private fun enableEditing(editGame: Game) {
        //set editing true
        editing = true

        //save old image name for comparison later
        oldImage = editGame.image.toString()

        val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.US)

        //change header
        binding.GameAddLabel.text = context?.getString(R.string.edit_game_label)

        //set values in fields
        binding.GameAddTitle.setText(editGame.title)
        binding.GameAddDev.setText(editGame.developer)
        binding.GameAddPlatform.setText(editGame.platform)
        binding.GameAddGenre.setText(editGame.genre)

        binding.GameAddRating.setSelection(editGame.rating!! - 1)

        val cDate = dateFormatter.format(Date(editGame.complete!!.toInstant().toEpochMilli()))
        completeDate = editGame.complete!!.toInstant().toEpochMilli()
        binding.GameAddCompDate.setText(cDate)

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

            //set values
            completeDate = myTimeZoneDate.toInstant().toEpochMilli()
            binding.GameAddCompDate.setText(date)

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


        //when in editing mode, imageName is set to old image name
        if (editing) imageName = editGame.image.toString()

        //if the user has selected an image to upload, imageName gets set to the randomly generated file name
        if (image != Uri.EMPTY ) imageName = fileName

        val game = Game(binding.GameAddTitle.text.toString(), binding.GameAddDev.text.toString(),binding.GameAddPlatform.text.toString(), binding.GameAddGenre.text.toString(), binding.GameAddRating.selectedItemPosition + 1, Date(completeDate), imageName)

        //if editing, set over existing game
        if (editing){
            val gameRef = db.collection("users").document(auth.currentUser!!.uid).collection("games").document(editID).set(game).addOnSuccessListener {
                Toast.makeText(context, "Game Updated", Toast.LENGTH_LONG).show()
                //check if the image has been updated, if so upload new image and delete old image
                if (game.image != oldImage && image != Uri.EMPTY){

                    val imageRef = storage.child(imageName)
                    imageRef.child(oldImage).delete()
                    imageRef.putFile(image)
                }
                val bundle = Bundle()
                bundle.putSerializable("game", game)
                bundle.putString("id", editID)
                setFragmentResult("game edited", bundle)
                Navigation.findNavController(view).popBackStack()

            }.addOnFailureListener {
                Toast.makeText(context, "Game Not Updated", Toast.LENGTH_SHORT).show()
            }
        } else {
            db.collection("users").document(auth.currentUser!!.uid).collection("games").add(game).addOnSuccessListener {
                Toast.makeText(context, "Game Added", Toast.LENGTH_LONG).show()
                if (image != Uri.EMPTY){
                    val imageRef = storage.child(imageName)
                    imageRef.putFile(image)
                }
                Navigation.findNavController(view).popBackStack()
            }.addOnFailureListener {
                Toast.makeText(context, "Game Not Added", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getRandomString(length: Int) : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return String(CharArray(length) { allowedChars.random() })
    }

    private fun validateInput(view: View){
        //maybe add more validation checking later
        if (!checkBlank()){
            
            saveGame(view)

        } else {
            Toast.makeText(requireActivity(), "All fields must be filled out", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkBlank(): Boolean{
        return (binding.GameAddTitle.text.isEmpty() || binding.GameAddDev.text.isEmpty() || binding.GameAddPlatform.text.isEmpty() || binding.GameAddGenre.text.isEmpty() || binding.GameAddRating.selectedItem == null || binding.GameAddCompDate.text.isEmpty())

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        Toast.makeText(requireActivity(), "Position is: $position", Toast.LENGTH_SHORT).show()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}