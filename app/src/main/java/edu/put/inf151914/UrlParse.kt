package edu.put.inf151914

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.w3c.dom.Element
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

class UrlParse(private val gameId: String) {
    private val apiUrl = "https://boardgamegeek.com/xmlapi2/thing?id=$gameId&stats=1"

    suspend fun parse(): GameInformation = withContext(Dispatchers.IO){
        val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc = docBuilder.parse(URL(apiUrl).openStream())

        val games = doc.getElementsByTagName("item")
        val game = games.item(0) as Element

        val gameID = game.getAttribute("id")
        val thumbnail = game.getElementsByTagName("thumbnail").item(0).textContent
        val image = game.getElementsByTagName("image").item(0).textContent
        val name = game.getElementsByTagName("name").item(0).attributes.getNamedItem("value").textContent
        val description = game.getElementsByTagName("description").item(0).textContent.trim()
        val yearPublished = game.getElementsByTagName("yearpublished").item(0).attributes.getNamedItem("value").textContent
        val minPlayers = game.getElementsByTagName("minplayers").item(0).attributes.getNamedItem("value").textContent
        val maxPlayers = game.getElementsByTagName("maxplayers").item(0).attributes.getNamedItem("value").textContent
        val playingTime = game.getElementsByTagName("playingtime").item(0).attributes.getNamedItem("value").textContent
        val minAge = game.getElementsByTagName("minage").item(0).attributes.getNamedItem("value").textContent
        val rank = game.getElementsByTagName("rank").item(0).attributes.getNamedItem("value").textContent


        GameInformation(
            gameID,
            thumbnail,
            image,
            name,
            description,
            yearPublished,
            minPlayers,
            maxPlayers,
            playingTime,
            minAge,
            rank
        )
    }
}