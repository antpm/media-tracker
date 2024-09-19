package ca.anthony.mediatracker.models

import com.google.firebase.firestore.Exclude
import java.io.Serializable
import java.util.Date

class Game(var title:String? = null, var developer:String? = null, var platform:String? = null, var genre:String? = null,
           var rating:Int? =null, var complete: Date? = null, var image:String? = null, @get:Exclude var id:String? = null ): Serializable{
}