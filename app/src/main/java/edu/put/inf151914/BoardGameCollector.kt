package edu.put.inf151914

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class BoardGameCollector(context: Context) {

    private val databaseHelper = DatabaseHelper(context)
    private val db: SQLiteDatabase = databaseHelper.writableDatabase

    fun close() {
        db.close()
    }

    fun addGame(game: BoardGame): Long {
        val values = ContentValues().apply {
            put("title", game.title)
            put("originalTitle", game.originalTitle)
            put("releaseYear", game.releaseYear)
            put("bggId", game.bggId)
        }
        return db.insert("games", null, values)
    }

    fun addAdditionGame(game: BoardGame): Long {
        val values = ContentValues().apply {
            put("title", game.title)
            put("originalTitle", game.originalTitle)
            put("releaseYear", game.releaseYear)
            put("bggId", game.bggId)
        }
        return db.insert("additions", null, values)
    }

    fun addImage(image: GameImage) {
        val values = ContentValues().apply {
            put("gameId", image.gameId)
            put("additionId", image.additionId)
            put("photoUri", image.photoUri)
            put("thumbnail",image.thumbnail)
        }
        db.insert("game_photos", null, values)
    }

    fun addAdditionImage(image: GameImageFile) {
        val values = ContentValues().apply {
            put("gameId", image.gameId)
            put("additionId", image.additionId)
            put("photoUri", image.PhotoUri)
        }
        db.insert("addition_photos", null, values)
    }

    @SuppressLint("Range")
    fun getAllGames(): List<BoardGame> {
        val boardGames = mutableListOf<BoardGame>()
        val cursor: Cursor = db.rawQuery("SELECT * FROM games", null)
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex("id"))
            val title = cursor.getString(cursor.getColumnIndex("title"))
            val originalTitle = cursor.getString(cursor.getColumnIndex("originalTitle"))
            val releaseYear = cursor.getInt(cursor.getColumnIndex("releaseYear"))
            val bggId = cursor.getInt(cursor.getColumnIndex("bggId"))
            val boardGame = BoardGame(id, title, originalTitle, releaseYear, bggId)
            boardGames.add(boardGame)
        }
        cursor.close()
        return boardGames
    }
    @SuppressLint("Range")
    fun getAllAdditionGames(): MutableList<BoardGame> {
        val boardAdditionGames = mutableListOf<BoardGame>()
        val cursor: Cursor = db.rawQuery("SELECT * FROM additions", null)
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex("id"))
            val title = cursor.getString(cursor.getColumnIndex("title"))
            val originalTitle = cursor.getString(cursor.getColumnIndex("originalTitle"))
            val releaseYear = cursor.getInt(cursor.getColumnIndex("releaseYear"))
            val bggId = cursor.getInt(cursor.getColumnIndex("bggId"))
            val boardAdditionGame = BoardGame(id, title, originalTitle, releaseYear, bggId)
            boardAdditionGames.add(boardAdditionGame)
        }
        cursor.close()
        return boardAdditionGames
    }
    @SuppressLint("Range")
    fun getGamesImage(gameId: Int): List<String> {
        val imageFiles = mutableListOf<String>()

        val query = "SELECT photoUri FROM addition_photos WHERE gameId = ?"
        val cursor = db.rawQuery(query, arrayOf(gameId.toString()))

        while (cursor.moveToNext()) {
            val imagePath = cursor.getString(cursor.getColumnIndexOrThrow("photoUri"))
            imageFiles.add(imagePath)
        }
        cursor.close()
        return imageFiles
    }
    @SuppressLint("Range")
    fun getAdditionGamesImage(gameId: Int): List<String> {
        val imageAdditionFiles = mutableListOf<String>()

        val query = "SELECT photoUri FROM addition_photos WHERE additionId = ?"
        val cursor = db.rawQuery(query, arrayOf(gameId.toString()))

        while (cursor.moveToNext()) {
            val imagePath = cursor.getString(cursor.getColumnIndexOrThrow("photoUri"))
            imageAdditionFiles.add(imagePath)
        }
        cursor.close()
        return imageAdditionFiles
    }
    @SuppressLint("Range")
    fun getBGG(gameId: Int): Int? {
        val query = "SELECT bggId FROM games WHERE id = ?"
        val selectionArgs = arrayOf(gameId.toString())
        val cursor = db.rawQuery(query, selectionArgs)

        var gameBggId: Int? = null
        if (cursor.moveToFirst()) {
            gameBggId = cursor.getInt(cursor.getColumnIndex("bggId"))
        }
        cursor.close()
        return gameBggId
    }
    @SuppressLint("Range")
    fun getAdditionBGG(gameId: Int): Int? {
        val query = "SELECT bggId FROM additions WHERE id = ?"
        val selectionArgs = arrayOf(gameId.toString())
        val cursor = db.rawQuery(query, selectionArgs)

        var additionGameBggId: Int? = null
        if (cursor.moveToFirst()) {
            additionGameBggId = cursor.getInt(cursor.getColumnIndex("bggId"))
        }
        cursor.close()
        return additionGameBggId
    }
    @SuppressLint("Range")
    fun getThumbnailByGameId(gameId: Int): String? {
        val query = "SELECT thumbnail FROM game_photos WHERE gameId = ?"
        val selectionArgs = arrayOf(gameId.toString())
        val cursor = db.rawQuery(query, selectionArgs)

        var thumbnail: String? = null
        if (cursor.moveToFirst()) {
            thumbnail = cursor.getString(cursor.getColumnIndex("thumbnail"))
        }
        cursor.close()
        return thumbnail
    }
    @SuppressLint("Range")
    fun getThumbnailByAdditionId(gameId: Int): String? {
        val query = "SELECT thumbnail FROM game_photos WHERE additionId = ?"
        val selectionArgs = arrayOf(gameId.toString())
        val cursor = db.rawQuery(query, selectionArgs)

        var thumbnail: String? = null
        if (cursor.moveToFirst()) {
            thumbnail = cursor.getString(cursor.getColumnIndex("thumbnail"))
        }
        cursor.close()
        return thumbnail
    }

    fun deleteGames() {
        db.delete("games", null, null)
    }
    fun deleteAdditionGame() {
        db.delete("additions", null, null)
    }
    fun deleteGameImages() {
        db.delete("game_photos", null, null)
    }
    fun deleteFilesGameImages() {
        db.delete("addition_photos", null, null)
    }
    fun deleteImageFile(imagePath: String) {
        db.delete("addition_photos", "photoUri = ?", arrayOf(imagePath.toString()))
    }
}