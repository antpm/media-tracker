package ca.anthony.mediatracker.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import ca.anthony.mediatracker.R
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.Date
import java.util.Locale

class GameAddFragment : Fragment() {

    private lateinit var releaseDateTxt: EditText
    private lateinit var completeDateTxt: EditText

    private var releaseDate: Long = 0
    private var completeDate:Long = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        releaseDateTxt = view.findViewById(R.id.GameAddReleaseDate)
        completeDateTxt = view.findViewById(R.id.GameAddCompDate)

        releaseDateTxt.setOnClickListener {
            showDatePicker(releaseDateTxt)
        }

        completeDateTxt.setOnClickListener {
            showDatePicker(completeDateTxt)
        }
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

}