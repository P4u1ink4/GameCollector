package edu.put.inf151914

import BoardGameListAdapter
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GameAct : AppCompatActivity(), BoardGameListAdapter.OnItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var boardGameListAdapter: BoardGameListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gamelist)

        initializeRecyclerView()

        val dataSource = BoardGameCollector(this)
        val boardGames = dataSource.getAllGames()

        boardGameListAdapter = BoardGameListAdapter(this, boardGames, Type.BOARDGAME)
        boardGameListAdapter.setOnItemClickListener(this)
        recyclerView.adapter = boardGameListAdapter
    }

    private fun initializeRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onItemClick(boardGameId: Int) {
        val intent = Intent(this, GameDetailAct::class.java)
        intent.putExtra("boardGameId", boardGameId)
        startActivity(intent)
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
