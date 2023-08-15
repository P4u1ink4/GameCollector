package edu.put.inf151914

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "board_game_collector.db"
        private const val DATABASE_VERSION = 1
    }

    // Tworzenie tabeli gier

    private val createGamesTable = "CREATE TABLE IF NOT EXISTS games " +
            "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "title TEXT, " +
            "originalTitle TEXT, " +
            "releaseYear INTEGER, " +
            "bggId INTEGER) "


    private val createAdditionGamesTable = "CREATE TABLE IF NOT EXISTS additions " +
            "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "title TEXT, " +
            "originalTitle TEXT, " +
            "releaseYear INTEGER, " +
            "bggId INTEGER) "

    // Tworzenie tabeli zdjęć gier

    private val createGamePhotosTable = "CREATE TABLE IF NOT EXISTS game_photos " +
            "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "gameId INTEGER, " +
            "additionId INTEGER, " +
            "thumbnail TEXT," +
            "photoUri TEXT, " +
            "FOREIGN KEY (gameId) REFERENCES games(id) ON DELETE CASCADE, " +
            "FOREIGN KEY (additionId) REFERENCES additions(id) ON DELETE CASCADE) "

    private val createAdditionGamePhotosTable = "CREATE TABLE IF NOT EXISTS addition_photos " +
            "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "gameId INTEGER, " +
            "additionId INTEGER, " +
            "photoUri TEXT, " +
            "FOREIGN KEY (gameId) REFERENCES games(id) ON DELETE CASCADE, " +
            "FOREIGN KEY (additionId) REFERENCES additions(id) ON DELETE CASCADE) "

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createGamesTable)
        db.execSQL(createAdditionGamesTable)
        db.execSQL(createGamePhotosTable)
        db.execSQL(createAdditionGamePhotosTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS games")
        onCreate(db)
    }
}