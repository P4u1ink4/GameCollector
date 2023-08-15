package edu.put.inf151914

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.random.Random

class Synchron : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.synchro)

        val cache = getSharedPreferences("cache", MODE_PRIVATE)
        val lastSyncDate: TextView = findViewById(R.id.LastSyncDate)
        lastSyncDate.text = "Ostatnia synchronizacja: " + cache.getString("syncDate", "")

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        var syncButton = findViewById<Button>(R.id.btnSync)

        syncButton.setOnClickListener(){
            val cache = getSharedPreferences("cache", MODE_PRIVATE)

            progressBar.max = 100
            val lastSyncLong = cache.getLong("syncDateLong", Instant.now().toEpochMilli())

            Log.d("TEST_SYNCHRO", lastSyncLong.toString())
            Log.d("TEST_SYNCHRO", Instant.now().minusSeconds(86400).toEpochMilli().toString())

            if (lastSyncLong < Instant.now().minusSeconds(86400).toEpochMilli()) {
                Thread(Runnable {

                    var url = "https://boardgamegeek.com/xmlapi2/collection?username=" + cache.getString(
                        "username",
                        ""
                    ) +
                            "&subtype=boardgame&excludesubtype=boardgameexpansion"
                    var boardgames = XMLParse().execute(url)
                    if (boardgames.get().isEmpty()) {
                        boardgames = XMLParse().execute(url)
                    }
                    runOnUiThread {
                        val number = Random.nextInt(0, 25)
                        progressBar.setProgress(number, true)
                    }
                    Thread.sleep(250)

                    url = "https://boardgamegeek.com/xmlapi2/collection?username=" + cache.getString(
                        "username",
                        ""
                    ) +
                            "&subtype=boardgameexpansion"
                    var additions = XMLParse().execute(url)
                    if (additions.get().isEmpty()) {
                        additions = XMLParse().execute(url)
                    }
                    runOnUiThread {
                        val number = Random.nextInt(26, 49)
                        progressBar.setProgress(number, true)
                    }
                    Thread.sleep(250)

                    val dataSource = BoardGameCollector(this)
                    dataSource.deleteGames()
                    dataSource.deleteAdditionGame()
                    dataSource.deleteGameImages()
                    runOnUiThread {
                        val number = Random.nextInt(50, 75)
                        progressBar.setProgress(number, true)
                    }
                    Thread.sleep(250)

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
                    runOnUiThread {
                        val number = Random.nextInt(76, 99)
                        progressBar.setProgress(number, true)
                    }
                    Thread.sleep(250)

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

                    runOnUiThread {
                        progressBar.setProgress(100, true)

                        val currentDate = LocalDate.now()
                        cache.edit().putString(
                            "syncDate", currentDate.format(
                                DateTimeFormatter.ofPattern("dd-MM-yyyy")
                            ).toString()
                        ).apply()
                        lastSyncDate.text = "Ostatnia synchronizacja: " + cache.getString("syncDate", "")
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                }).start()
            } else {
                // Data is already synchronized
            }
        }
    }


    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
