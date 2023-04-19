package com.example.moviebrowser

sealed class GameState {
    class Running(val underscoreWord: String,
                  val drawable: Int) : GameState()
    class Lost(val wordToGuess: String) : GameState()
    class Won(val wordToGuess: String) : GameState()
}