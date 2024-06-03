package ca.anthony.mediatracker.models

import java.io.Serializable

class Game(var developer:String? = null, var genre:String? = null, var image:String? = null, var platform:String? = null, var publisher:String? = null, var rating:Int? =null, var release:Long? = null, var title:String? = null, var complete:Long? = null):Serializable {
}