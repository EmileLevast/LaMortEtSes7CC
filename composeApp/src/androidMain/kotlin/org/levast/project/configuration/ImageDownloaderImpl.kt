package org.levast.project.configuration

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import configuration.IConfiguration
import network.IImageDownloader
import java.io.IOException
import java.net.URL


class ImageDownloaderImpl(val config: IConfiguration) : IImageDownloader{

    private var imageBackground: ImageBitmap? = null

    val endpoint get() = config.getEndpointServer()

    private fun loadNetworkImage(link: String, format: String): ImageBitmap {
        val url = URL(link)
        try {
            return BitmapFactory.decodeStream(url.openConnection().getInputStream()).asImageBitmap()
        } catch (e: IOException) {
            println(e)
            return ImageBitmap(10,10)//une image vide
        }

    }

    override fun downloadBackgroundImage(urlImage: String): ImageBitmap {
        return if (imageBackground == null) {
            val format = urlImage.substring(urlImage.lastIndexOf(".") + 1)
            loadNetworkImage(urlImage, format)
        } else {
            imageBackground!!
        }
    }

    private fun downloadImageWithUrl(urlImage: String): ImageBitmap {
        val format = urlImage.substring(urlImage.lastIndexOf(".") + 1)
        return loadNetworkImage(urlImage, format)
    }

    override fun downloadImageWithName(imageNameWithExtension: String): ImageBitmap? {
        return try {
            downloadImageWithUrl(getUrlImageWithFileName(imageNameWithExtension))
        } catch (e: Exception) {
            null
        }
    }

    private fun getUrlImageWithFileName(fileName: String) = "$endpoint/images/$fileName"

}