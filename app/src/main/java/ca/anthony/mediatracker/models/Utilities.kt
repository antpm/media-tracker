package ca.anthony.mediatracker.models

import android.content.Context

//class contains functions used throughout multiple fragments in the app
class Utilities(context: Context) {
    private var mContext: Context? = context

    public fun getRandomString(length: Int) : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return String(CharArray(length) { allowedChars.random() })
    }

}