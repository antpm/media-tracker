package ca.anthony.mediatracker.activities

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.ui.setupWithNavController
import androidx.navigation.fragment.NavHostFragment
import ca.anthony.mediatracker.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        Log.d("Test", "OnCreate Fired")

        val navContainer = supportFragmentManager.findFragmentById(R.id.NavHost) as NavHostFragment
        val navController = navContainer.navController

        val bottomNav: BottomNavigationView = findViewById(R.id.BottomNav)
        bottomNav.setupWithNavController(navController)
    }
}