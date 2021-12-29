package com.zlatamigas.animind

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.zlatamigas.animind.databinding.ActivityMainBinding
import com.zlatamigas.animind.controller.db.DBController
import com.zlatamigas.animind.controller.db.DBHelper

class MainActivity : AppCompatActivity() {

    var dbController: DBController? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val helper = DBHelper(this)

        dbController = DBController(helper)
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)
    }
}
