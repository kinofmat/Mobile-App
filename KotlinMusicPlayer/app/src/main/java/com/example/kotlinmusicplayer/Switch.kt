package com.example.kotlinmusicplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

// The class I created again, in trying to model my change views feature based off of the example module in class.
class Switch : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}