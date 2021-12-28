package com.zlatamigas.testbottomnavigation

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.zlatamigas.testbottomnavigation.databinding.ActivityMainBinding

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

class DBHelper(context: Context): SQLiteOpenHelper(context,
    "AnimeDB", null, 1){

    private val FAVOURITES_TABLE_NAME = "favourites"
    private val REMINDERS_TABLE_NAME = "reminders"
    private val COLUMN_ID = "id"
    private val COLUMN_ROW_ID = "row_id"
    private val COLUMN_NAME = "name"
    private val COLUMN_ADD_DATE = "add_date"
    private val COLUMN_NOTIFY_DATE = "notify_date"

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_FAVOURITES_TABLE = ("CREATE TABLE $FAVOURITES_TABLE_NAME ("
                + "$COLUMN_ID INTEGER PRIMARY KEY,"
                + "$COLUMN_NAME TEXT,"
                + "$COLUMN_ADD_DATE TEXT)")

        val CREATE_REMINDERS_TABLE = ("CREATE TABLE $REMINDERS_TABLE_NAME ("
                + "$COLUMN_ROW_ID INTEGER PRIMARY KEY,"
                + "$COLUMN_ID INTEGER,"
                + "$COLUMN_NAME TEXT,"
                + "$COLUMN_NOTIFY_DATE TEXT)")

        db?.execSQL(CREATE_FAVOURITES_TABLE)
        db?.execSQL(CREATE_REMINDERS_TABLE)
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}
}