package edu.put.inf151914

import android.os.AsyncTask
import android.util.Log
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory

class XMLParse : AsyncTask<String, Void, List<BoardGameDetail>>() {

    override fun doInBackground(vararg urls: String?): List<BoardGameDetail> {
        val url = java.net.URL(urls[0])
        val connection = url.openConnection() as java.net.HttpURLConnection
        connection.readTimeout = 10000
        connection.connectTimeout = 15000
        connection.requestMethod = "GET"
        connection.doInput = true

        try {
            connection.connect()
            return parseXml(connection.inputStream)
        } catch (e: Exception) {
            Log.e("XMLParse", "Error during parsing", e)
        } finally {
            connection.disconnect()
        }
        return emptyList()
    }

    private fun parseXml(inputStream: InputStream): List<BoardGameDetail> {
        val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc = docBuilder.parse(inputStream)
        doc.documentElement.normalize()

        val itemList = doc.getElementsByTagName("item")
        val boardGames = mutableListOf<BoardGameDetail>()
        for (i in 0 until itemList.length) {
            val itemNode = itemList.item(i)
            if (itemNode.nodeType == org.w3c.dom.Node.ELEMENT_NODE) {
                val elem = itemNode as org.w3c.dom.Element

                val nameElement = elem.getElementsByTagName("name").item(0)
                val name = nameElement?.textContent ?: ""

                val yearPublishedElement = elem.getElementsByTagName("yearpublished").item(0)
                val releaseYear = yearPublishedElement?.textContent?.toIntOrNull() ?: 0

                val imageElement = elem.getElementsByTagName("image").item(0)
                val image = imageElement?.textContent ?: ""

                val thumbnailElement = elem.getElementsByTagName("thumbnail").item(0)
                val thumbnail = thumbnailElement?.textContent ?: ""

                val boardGame = BoardGameDetail(
                    elem.getAttribute("objectid").toInt(),
                    name,
                    name,
                    releaseYear,
                    image,
                    thumbnail
                )
                boardGames.add(boardGame)
            }
        }
        return boardGames
    }
}
