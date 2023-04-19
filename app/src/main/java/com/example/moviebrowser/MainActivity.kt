package com.example.moviebrowser

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.example.moviebrowser.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val gameHelper = HangManHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStartNew.setOnClickListener {
            startNewGame()
        }

        var gameState = gameHelper.startNewGame()
        updateUI(gameState)

        binding.lettersLayout.children.forEach { letterView ->
            if (letterView is TextView) {
                letterView.setOnClickListener {
                    val gameState = gameHelper.play((letterView).text[0])
                    updateUI(gameState)
                    letterView.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun updateUI(gameState: GameState) {
        when (gameState) {
            is GameState.Lost -> showGameLost(gameState.wordToGuess)
            is GameState.Running -> {
                binding.userinput.text = gameState.underscoreWord
                binding.imageview.setImageResource(gameState.drawable)
                playVideo(gameHelper.drawableVideo)
            }
            is GameState.Won -> showGameWon(gameState.wordToGuess)
        }
    }

    private fun playVideo(res: Int) {
        if (binding.videoplayer.isPlaying.not()) {
            binding.videoplayer.setVideoURI(Uri.parse("android.resource://$packageName/${res}"))
            binding.videoplayer.start()
            timer()
        }
    }

    //below function to play trailer only 10 seconds
    private fun timer() {
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                try {
                    val seconds = (binding.videoplayer.currentPosition % (1000 * 60 * 60) % (1000 * 60) / 1000) as Int
                    if (seconds > Constants.trailerSeconds) {
                        //binding.videoplayer.stopPlayback()
                        binding.videoplayer.seekTo(0)
                        binding.videoplayer.start()
                    }

                } catch (e: Exception) {
                }
            }
        }, 0, 1000)
    }



    private fun showGameLost(wordToGuess: String) {
        showAlert("Unfortunately!!!" , "You Lost")
        binding.videoplayer.stopPlayback()
        binding.userinput.text = wordToGuess
        binding.lettersLayout.visibility = View.GONE
    }

    private fun showGameWon(wordToGuess: String) {
        showAlert("Hooray!!!" , "You won")
        binding.videoplayer.stopPlayback()
        binding.userinput.text = wordToGuess
        binding.lettersLayout.visibility = View.GONE
    }

    private fun showAlert(title: String, msg: String) {
        val alert = AlertDialog.Builder(this)
        alert.setTitle(title)
        alert.setMessage(msg)
        alert.setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
            println("ok clicked")
        })
        alert.show()
    }

    private fun startNewGame() {
        binding.videoplayer.stopPlayback()
        val gameState = gameHelper.startNewGame()
        binding.lettersLayout.visibility = View.VISIBLE
        binding.lettersLayout.children.forEach { letterView ->
            letterView.visibility = View.VISIBLE
        }
        updateUI(gameState)
    }

    override fun onResume() {
        super.onResume()
        binding.videoplayer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.videoplayer.stopPlayback()
    }

}