package edu.put.inf151914

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Configuration : AppCompatActivity() {
    private var setUsernameEditText: EditText? = null
    private lateinit var syncDataButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.configure)
        val cache = getSharedPreferences("cache", Context.MODE_PRIVATE)

        if(cache.getBoolean("configDone", false)) {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }
        else{
            setContentView(R.layout.configure)
            setUsernameEditText = findViewById(R.id.setUsername)
            syncDataButton = findViewById(R.id.btnConfigure)

            syncDataButton.setOnClickListener{
                val cache = getSharedPreferences("cache", Context.MODE_PRIVATE)
                val editText = findViewById<EditText>(R.id.setUsername)
                val username = editText.text.toString()
                cache.edit().putString("username",username).apply()
                cache.edit().putBoolean("configDone", true).apply()
                val dataSource = BoardGameCollector(this)
                dataSource.deleteGames()
                dataSource.deleteAdditionGame()
                dataSource.deleteGameImages()
                dataSource.deleteFilesGameImages()

                var url = "https://boardgamegeek.com/xmlapi2/collection?username="+cache.getString("username","")+"&subtype=boardgame&excludesubtype=boardgameexpansion"
                val boardgames = XMLParse().execute(url)

                url = "https://boardgamegeek.com/xmlapi2/collection?username="+cache.getString("username","")+"&subtype=boardgameexpansion"
                val additions = XMLParse().execute(url)

                for (addition in additions.get()) {
                    val newAdditionId = dataSource.addAdditionGame(
                        BoardGame(
                            id = 0,
                            title = addition.title,
                            originalTitle = addition.title,
                            releaseYear = addition.releaseYear,
                            bggId = addition.bggId
                        )
                    )
                    dataSource.addImage(
                        GameImage(
                            additionId = newAdditionId.toInt(),
                            gameId = null,
                            photoUri = addition.image,
                            thumbnail = addition.thumbnail
                        )
                    )
                }

                for (boardGame in boardgames.get()) {
                    val newBoardGameId = dataSource.addGame(
                        BoardGame(
                            id = 0,
                            title = boardGame.title,
                            originalTitle = boardGame.title,
                            releaseYear = boardGame.releaseYear,
                            bggId = boardGame.bggId
                        )
                    )
                    dataSource.addImage(
                        GameImage(
                            additionId = null,
                            gameId = newBoardGameId.toInt(),
                            photoUri = boardGame.image,
                            thumbnail = boardGame.thumbnail
                        )
                    )
                }
                dataSource.close()
                val currentDate = LocalDate.now()
                val dateString = currentDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")).toString()
                cache.edit().putString("syncDate", currentDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")).toString()).apply()
                cache.edit().putBoolean("firstSync", true).apply()
                cache.edit().putLong("syncDateLong", Instant.now().toEpochMilli()).apply()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

    }
}
