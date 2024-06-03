package ca.anthony.mediatracker.models

import java.io.Serializable
import java.util.Date

class Game(var developer:String? = null, var genre:String? = null, var image:String? = null, var platform:String? = null, var publisher:String? = null, var rating:Int? =null, var release:Date? = null, var title:String? = null, var complete: Date? = null):Serializable {
}