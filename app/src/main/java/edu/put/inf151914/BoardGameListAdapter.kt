import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import edu.put.inf151914.BoardGame
import edu.put.inf151914.BoardGameCollector
import edu.put.inf151914.R
import edu.put.inf151914.Type
import java.io.File

class BoardGameListAdapter(private val context: Context, private val boardGames: List<BoardGame>, private val type: Type) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val HEADER_VIEW_TYPE = 0
    private val ITEM_VIEW_TYPE = 1
    private var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(boardGameId: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HEADER_VIEW_TYPE -> {
                val headerView:View = if(type.equals(Type.BOARDGAME)) {
                    LayoutInflater.from(context).inflate(R.layout.gamelayout, parent, false)
                } else {
                    LayoutInflater.from(context).inflate(R.layout.additionlayout, parent, false)
                }
                HeaderViewHolder(headerView)
            }
            ITEM_VIEW_TYPE -> {
                val itemView = LayoutInflater.from(context).inflate(R.layout.item_board_game_list, parent, false)
                ItemViewHolder(itemView)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> {
                // No binding needed for header view
            }
            is ItemViewHolder -> {
                val itemPosition = position - 1 // Subtract 1 to account for header view
                val boardGame = boardGames[itemPosition]
                holder.bind(boardGame)
            }
        }
    }

    override fun getItemCount(): Int {
        // Item count is number of board games plus 1 for the header view
        return boardGames.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            HEADER_VIEW_TYPE
        } else {
            ITEM_VIEW_TYPE
        }
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val text1: TextView = itemView.findViewById(R.id.text1)
        private val image1: ImageView = itemView.findViewById(R.id.image1)
        private val textTitle: TextView = itemView.findViewById(R.id.textTitle)
        private val textreleaseYear: TextView = itemView.findViewById(R.id.textreleaseYear)

        fun bind(boardGame: BoardGame) {
            text1.text = null
            textTitle.text = null
            textreleaseYear.text = null
            image1.setImageDrawable(null)

            val itemPosition = adapterPosition - 1
            text1.text = (1+itemPosition).toString()
            textTitle.text = boardGame.title
            textreleaseYear.text = "Rok publikacji: " + boardGame.releaseYear.toString()

            val dataSource = BoardGameCollector(context)
            var thumbnailUrl: String = ""

            if (type.equals(Type.BOARDGAME)) {
                val imageFileForThumbnail = dataSource.getGamesImage(boardGame.id)
                if (imageFileForThumbnail.isNotEmpty()) {
                    val file = File(context.getExternalFilesDir(null), "images/${imageFileForThumbnail[0]}")
                    if (file.exists()) {
                        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                        image1.setImageBitmap(bitmap)
                    }
                } else {
                    thumbnailUrl = dataSource.getThumbnailByGameId(boardGame.id).toString()
                    if (!thumbnailUrl.isNullOrEmpty()) {
                        Picasso.get().load(thumbnailUrl).into(image1)
                    }
                }
            } else {
                val imageFileForThumbnail = dataSource.getAdditionGamesImage(boardGame.id)
                if (imageFileForThumbnail.isNotEmpty()) {
                    val file = File(context.getExternalFilesDir(null), "images/${imageFileForThumbnail[0]}")
                    if (file.exists()) {
                        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                        image1.setImageBitmap(bitmap)
                    }
                } else {
                    thumbnailUrl = dataSource.getThumbnailByAdditionId(boardGame.id).toString()
                    if (!thumbnailUrl.isNullOrEmpty()) {
                        Picasso.get().load(thumbnailUrl).into(image1)
                    }
                }
            }
            itemView.setOnClickListener {
                val boardGameId = boardGame.id
                onItemClickListener?.onItemClick(boardGameId)
            }

        }
    }
}
