package com.example.uploader.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity // Changed from ComponentActivity
import androidx.navigation.fragment.NavHostFragment
// import androidx.navigation.ui.setupActionBarWithNavController // Uncomment if you have an ActionBar
import com.example.uploader.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() { // Changed to AppCompatActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Set content from XML

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        // If you have an ActionBar and want to integrate navigation with it:
        // setupActionBarWithNavController(navController)
    }

    // If using setupActionBarWithNavController, you might also need to override onSupportNavigateUp:
    // override fun onSupportNavigateUp(): Boolean {
    //     val navController = findNavController(R.id.nav_host_fragment) // or import androidx.navigation.findNavController
    //     return navController.navigateUp() || super.onSupportNavigateUp()
    // }
}
