package ca.anthony.mediatracker.models

import java.io.Serializable
import java.util.Date

class Book(var title:String? = null, var author:String? = null, var genre:String? = null, var publisher:String? = null, var rating:Int? =null,
           var release: Date? = null, var complete: Date? = null, var image:String? = null): Serializable {
}