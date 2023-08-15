package edu.put.inf151914

import BoardGameListAdapter
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AdditionAct : AppCompatActivity(), BoardGameListAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var boardGameListAdapter: BoardGameListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.additionlist)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val dataSource = BoardGameCollector(this)
        val additions = dataSource.getAllAdditionGames()

        boardGameListAdapter = BoardGameListAdapter(this, additions, Type.ADDITIONS)
        boardGameListAdapter.setOnItemClickListener(this)
        recyclerView.adapter = boardGameListAdapter
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onItemClick(additionId: Int) {
        val intent = Intent(this, AdditionDetailAct::class.java)
        intent.putExtra("additionId", additionId)
        startActivity(intent)
    }
}
