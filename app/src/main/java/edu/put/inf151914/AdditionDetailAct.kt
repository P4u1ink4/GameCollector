package edu.put.inf151914

import android.Manifest.permission.*
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.text.Html
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL


class AdditionDetailAct : AppCompatActivity() {
    private val images = mutableListOf<Bitmap>()
    private var currentImageIndex = 0

    private var DEFAULT_IMAGE_BITMAP: Bitmap? = null

    private val CAMERA_PERMISSION_REQUEST_CODE = 1001
    private val GALLERY_PERMISSION_REQUEST_CODE = 1002
    private val CAMERA_REQUEST_CODE = 2001
    private val GALLERY_REQUEST_CODE = 2002
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.details)

        val additionId = intent.getIntExtra("additionId", 0)

        val dataSource = BoardGameCollector(this)
        val boardGameBggId = dataSource.getAdditionBGG(additionId)

        val boardGameTitleDetails = findViewById<TextView>(R.id.DetalGameTitle)
        val rank = findViewById<TextView>(R.id.detailRank)
        val publishYear = findViewById<TextView>(R.id.detailPublish)
        val detailNumber = findViewById<TextView>(R.id.detailNumber)
        val detailTime = findViewById<TextView>(R.id.detailTime)
        val detailDescription = findViewById<TextView>(R.id.detailOpis)


        val xmlParser = UrlParse(boardGameBggId.toString())
        GlobalScope.launch {
            val gameDetails = xmlParser.parse()
            runOnUiThread {
                loadImage(gameDetails.image)
                loadImageFilesFromDatabase(additionId)
                boardGameTitleDetails.text = gameDetails.name
                rank.text = "Pozycja według globalnego rankingu: " + gameDetails.rank
                publishYear.text = "Rok publikacji: " + gameDetails.yearPublished
                detailNumber.text = "Ilość graczy: " + gameDetails.minPlayers + " - " + gameDetails.maxPlayers
                detailTime.text = "Średni czas gry w minutach: " + gameDetails.playingTime
                detailDescription.text = "Opis: " + decodeHtmlText(gameDetails.description)

                initUI()
            }
        }
    }
    private fun initUI() {
        showImage()
        val buttonNext = findViewById<Button>(R.id.btnViewPhoto)
        val buttonAdd = findViewById<Button>(R.id.btnAddPhoto)
        val buttonRemove = findViewById<Button>(R.id.btnDeletePhoto)
        val imageView = findViewById<ImageView>(R.id.detalGameImage)

        buttonNext.setOnClickListener {
            showNextImage()
        }

        buttonAdd.setOnClickListener {
            showImageSourceSelection()
        }

        buttonRemove.setOnClickListener {
            removeCurrentImage()
        }

        imageView.setOnClickListener {
            showImagePreviewDialog()
        }
    }
    fun decodeHtmlText(htmlText: String): String {
        val decodedText = Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY)
        return decodedText.toString()
    }
    private fun showImagePreviewDialog() {
        val currentBitmap = images[currentImageIndex]

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.image)

        val imageViewPreview = dialog.findViewById<ImageView>(R.id.imagePreview)
        imageViewPreview.setImageBitmap(currentBitmap)
        dialog.show()
    }
    private fun saveImage(bitmap: Bitmap, resourceId: Int) {
        val dataSource = BoardGameCollector(this)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArray = stream.toByteArray()
        stream.close()

        val imagesDir = File(getExternalFilesDir(null), "game_photos")
        imagesDir.mkdirs()

        val fileName = "image_${resourceId}_${System.currentTimeMillis()}.jpg"
        val file = File(imagesDir, fileName)
        val outputStream = FileOutputStream(file)
        outputStream.write(byteArray)
        outputStream.close()
        dataSource.addAdditionImage(GameImageFile(
            resourceId,
            null,
            fileName
        ))

    }
    private fun loadImage(imageUrl: String) {
        GlobalScope.launch {
            try {
                val inputStream = URL(imageUrl).openStream()
                val bitmap = BitmapFactory.decodeStream(inputStream)
                DEFAULT_IMAGE_BITMAP = bitmap
                withContext(Dispatchers.Main) {
                    images.add(bitmap)
                    runOnUiThread {
                        val imageView = findViewById<ImageView>(R.id.detalGameImage)
                        imageView.setImageBitmap(images[currentImageIndex])
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    private fun loadImageFilesFromDatabase(additionId: Int) {
        val dataSource = BoardGameCollector(this)
        val imageFiles = dataSource.getGamesImage(additionId)

        for (imageFile in imageFiles) {
            val file = File(getExternalFilesDir(null), "images/${imageFile}")
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                images.add(bitmap)
            }
        }
    }
    private fun addImage(bitmap: Bitmap) {
        val gameId = intent.getIntExtra("additionId", 0)
        saveImage(bitmap, gameId)
        images.add(bitmap)
        showImage()
    }
    private fun removeCurrentImage() {
        if (images.size > 1) {
            val additionId = intent.getIntExtra("additionId", 0)
            val dataSource = BoardGameCollector(this)

            val imagePaths = dataSource.getAdditionGamesImage(additionId)
            var imagePath:String

            if(currentImageIndex >= imagePaths.size) {
                imagePath = imagePaths[currentImageIndex-1]
            } else {
                imagePath = imagePaths[currentImageIndex]
            }

            if(images[currentImageIndex].equals(DEFAULT_IMAGE_BITMAP)) {
            } else {
                // delete record from database
                dataSource.deleteImageFile(imagePath)
                // delete file from directory
                val imagesDir = File(getExternalFilesDir(null), "game_photos")
                val file = File(imagesDir, imagePath)
                file.delete()
                images.removeAt(currentImageIndex)
                currentImageIndex-=1
            }

            showImage()
        }
    }
    private fun showNextImage() {
        if (images.isNotEmpty()) {
            currentImageIndex = (currentImageIndex - 1 + images.size) % images.size
            showImage()
        }
    }
    private fun showImage() {
        val imageView = findViewById<ImageView>(R.id.detalGameImage)
        if (images.isNotEmpty()) {

            if(currentImageIndex == images.size) {
                currentImageIndex -= 1
            }
            if (currentImageIndex == -1)
                currentImageIndex = 0

            val bitmap = images[currentImageIndex]
            imageView.setImageBitmap(bitmap)
        } else {
            imageView.setImageDrawable(null)
        }
    }
    private fun showImageSourceSelection() {
        val items = arrayOf("Camera", "Gallery")
        val dialog = AlertDialog.Builder(this)
            .setTitle("Select Image Source")
            .setItems(items) { _, which ->
                when (which) {
                    0 -> {
                        // permission for camera
                        if (ContextCompat.checkSelfPermission(
                                this,
                                CAMERA
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                this,
                                arrayOf(CAMERA),
                                CAMERA_PERMISSION_REQUEST_CODE
                            )
                        } else {
                            openCamera()
                        }
                    }
                    1 -> {
                        // permission for gallery
                        if (ContextCompat.checkSelfPermission(
                                this,
                                READ_EXTERNAL_STORAGE
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                this,
                                arrayOf(READ_EXTERNAL_STORAGE),
                                GALLERY_PERMISSION_REQUEST_CODE
                            )
                        } else {
                            openGallery()
                        }
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }
    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                }
            }
            GALLERY_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery()
                }
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    val bitmap = data?.extras?.get("data") as? Bitmap
                    bitmap?.let {
                        addImage(it)
                    }
                }
                GALLERY_REQUEST_CODE -> {
                    val uri = data?.data
                    uri?.let {
                        val inputStream = contentResolver.openInputStream(uri)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        inputStream?.close()
                        bitmap?.let {
                            addImage(it)
                        }
                    }
                }
            }
        }
    }
    override fun onBackPressed() {
        val intent = Intent(this, AdditionAct::class.java)
        startActivity(intent)
    }

}