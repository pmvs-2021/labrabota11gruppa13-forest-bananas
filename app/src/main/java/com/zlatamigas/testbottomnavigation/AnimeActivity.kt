package com.zlatamigas.testbottomnavigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class AnimeActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anime)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}