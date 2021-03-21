package com.example.kotlinmusicplayer // Tells where to look for files at.

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var music: MediaPlayer //MediaPlayer is used to do as it says, play media. It cannot be null.
    private var totalTime: Int = 0
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play) // Use the activity_play layout as our first layout.
        
        /*val changeScreens = findViewById<Button>(R.id.change_screens)
        changeScreens.setOnClickListener { setContentView(R.layout.activity_play) }*/
        //setContentView(R.layout.activity_play)

        //Tell the mediaplayer what file it will be using. It will loop at the end of the song, and sets up other functionality.
        music = MediaPlayer.create(this, R.raw.lives)
        true.also { music.isLooping = it }
        music.setVolume(0.5f, 0.5f)
        totalTime = music.duration

        //Initializes the volumeBar.
        volumeUpdater()

        // Initializes the playBar.
        val playBar = findViewById<SeekBar>(R.id.playBar)
        playBar.max = totalTime // Sets the total length of bar.
        playBar.setOnSeekBarChangeListener( // Sets up for touch user input.
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) music.seekTo(progress) // Update to where the user touched
                }
                // Starts and then stops as the functions indicate.
                override fun onStartTrackingTouch(p0: SeekBar?) {}
                override fun onStopTrackingTouch(p0: SeekBar?) {}
            }
        )

        // Creating the instance for the music to be streamed from.
        Thread {
            while (true) {
                try {
                    val data = Message()
                    data.what = music.currentPosition // Says that we are using for the message
                    handler.sendMessage(data)
                    Thread.sleep(1000) // Wait 1 second to update again.
                } catch (e: InterruptedException) {}
            }
        }.start() // Starts playing the music
    }

    // Will set up the playBar to track the song's progress.
    @SuppressLint("HandlerLeak")
    var handler = object : Handler() {
        override fun handleMessage(data: Message) {
            val currentPosition = data.what
            // For initializing objects from the view make sure to tell what kind of object it was in the view.
            val playBar = findViewById<SeekBar>(R.id.playBar)

            playBar.progress = currentPosition // As the song progesses this will keep the playBar moving.

            // Update Labels
            val elapsedTimeLabel = findViewById<TextView>(R.id.elapsedTimeLabel) //R.id is needed before the id we gave the object.
            val elapsedTime = createTimeStamp(currentPosition)
            elapsedTimeLabel.text = elapsedTime // Update how much time has passed in the song.

            val remainingTimeLabel = findViewById<TextView>(R.id.remainingTimeLabel)
            val remainingTime = createTimeStamp(totalTime - currentPosition)
            remainingTimeLabel.text = "-" + remainingTime // Update how much time is left in the song.
        }
    }

    /*Created a separate function for updating the volumeBar.
    This was for when I was trying to mess around with getting the volume buttons to work.
    It's private to make sure that only the MainActivity class will have access to it.*/
    private fun volumeUpdater(){
        val volumeBar = findViewById<SeekBar>(R.id.volumeBar)
        volumeBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekbar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        val volume = progress / 100.0f
                        music.setVolume(volume, volume)
                    }
                }
                override fun onStartTrackingTouch(p0: SeekBar?) {}
                override fun onStopTrackingTouch(p0: SeekBar?) {}
            }
        )
    }

    //This function takes raw numerical input and create a nice styled time stamp.
    fun createTimeStamp(time: Int): String {
        var timeStamp: String
        val min = time / 1000 / 60 // First find the minutes.
        val sec = time / 1000 % 60 // Second find the seconds.

        timeStamp = "$min:" // Will correctly display 0, when the remaining time is only in seconds.
        // If there is less ten seconds, to keep the timeStamp formatted correctly we will add an extra 0 before the single digits.
        if (sec < 10) timeStamp += "0"
        timeStamp += sec
        return timeStamp
    }

    /*
    The playClick function was made with the help of the activity_play view.
    When the user clicks on the button it will call this fucntion to determine what to do.
    This will either play or pause the music and switch the image showed on the button accordingly.
    */
    fun playClick(view: View) {
        val playBtn = findViewById<Button>(R.id.playBtn)

        //If the music is playing then we need to pause it, and switch to a play button.
        if (music.isPlaying) {
            music.pause() // Uses the MediaPlayer to pause the music.
            playBtn.setBackgroundResource(R.drawable.play)
        // Else then we need to start the music and switch to a pause button.
        } else {
            music.start() // Uses the MediaPlayer to play the music.
            playBtn.setBackgroundResource(R.drawable.pause)
        }
    }

    /*
    Similarly this is declared with the stop button in the activity_play view.
    It will stop the music, and change the image on the play/pause button to play.
    */
    fun stopClick(view: View) {
        val playBtn = findViewById<Button>(R.id.playBtn)
        music.stop() // Stops the music.
        music = MediaPlayer.create(this, R.raw.lives) //We have to create a new MediaPlayer to be able to play the song again.
        true.also { music.isLooping = it } // Since we created a new one, we need to tell it, that lopping back through the song is allowed again.
        playBtn.setBackgroundResource(R.drawable.play)

    }

    /*
    This function is supposed to lower the volume if the quieter button is clicked.
    It will be a nice user interface feature for someone who just wants it a little bit quieter, and may have a hard time with the slider.
    It can also be for those who may be confused by the slider and just want to turn the volume down.
    */
    fun quietClick(view: View) {
        val volumeBar = findViewById<SeekBar>(R.id.volumeBar)
        val progress = volumeBar.progress // Gets the current volume position.
        val volumeNum = progress / 100.0f
        //These two will set up for moving the bar 5 units to the left, which would make it quieter.
        val left = volumeNum - 5
        val right = volumeNum + 5
        music.setVolume(left, right) // Supposed to set the bar to the new position.
    }

    /*
    This function is supposed to be the same as the quietClick, but instead will make it louder.
    Neither of these actually work, and I have not quite figured out how to make them work.
    */
    fun loudClick(view: View) {
        val volumeBar = findViewById<SeekBar>(R.id.volumeBar)
        val progress = volumeBar.progress
        val volumeNum = progress / 100.0f
        val left = volumeNum + 5
        val right = volumeNum - 5
        music.setVolume(left, right)

    }

    // An attempt made to change the activity_main view to the activity_play view based off of a button to switch the screens.
    fun changeClick(view: View) {
        setContentView(R.layout.activity_play)
    }

    // Ended up taking a lot of the code out of this, but this is from trying to model my code off of the code we made from class.
    private fun changeView() {
        Switch::class
    }
}