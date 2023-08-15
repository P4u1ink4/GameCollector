package edu.put.inf151914

import  android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate


class MainActivity : AppCompatActivity() {
    private lateinit var syncDataButton: Button
    private lateinit var gameButton: Button
    private lateinit var additionButton: Button
    private lateinit var cleardata: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        val cache = getSharedPreferences("cache", Context.MODE_PRIVATE)
        starting(cache)

        syncDataButton = findViewById(R.id.SynchroButton)
        gameButton = findViewById(R.id.GameButton)
        additionButton = findViewById(R.id.AdditionButton)
        cleardata = findViewById(R.id.CleanButton)

        syncDataButton.setOnClickListener{
            val intent = Intent(this, Synchron::class.java)
            startActivity(intent)
        }
        gameButton.setOnClickListener{
            val intent = Intent(this, GameAct::class.java)
            startActivity(intent)
        }
        additionButton.setOnClickListener{
            val intent = Intent(this, AdditionAct::class.java)
            startActivity(intent)
        }
        cleardata.setOnClickListener{
            val cache = getSharedPreferences("cache", Context.MODE_PRIVATE)
            cache.edit().putBoolean("firstSync", false).apply()
            cache.edit().putBoolean("configDone", false).apply()
            val dataSource = BoardGameCollector(this)
            dataSource.deleteAdditionGame()
            dataSource.deleteGames()
            dataSource.deleteGameImages()
            dataSource.deleteFilesGameImages()
            val intent = Intent(this, Configuration::class.java)
            startActivity(intent)
        }

    }
    override fun onResume() {
        super.onResume()
        val cache = getSharedPreferences("cache", Context.MODE_PRIVATE)
        starting(cache)
    }

    private fun starting(cache: SharedPreferences) {

        val dataSource = BoardGameCollector(this)

        val username = findViewById<TextView>(R.id.GamerName)
        username.text = "Nazwa użytkownika:" + cache.getString("username", "")

        val additions: TextView = findViewById(R.id.AdditionNumber)
        additions.text = "Ilość posiadanych dodatków:" + dataSource.getAllAdditionGames().size

        val games: TextView = findViewById(R.id.GamesNumber)
        games.text = "Ilość posiadanych gier:" + dataSource.getAllGames().size

        val lastSyncDate: TextView = findViewById(R.id.LastSynchro)
        lastSyncDate.text = "Ostatnia synchronizacja:" + cache.getString("syncDate", "")
    }

}