package org.levast.project.configuration

import androidx.compose.ui.graphics.ImageBitmap
import configuration.IConfiguration
import network.IImageDownloader


class ImageDownloaderImpl(val config: IConfiguration) : IImageDownloader{

    private var imageBackground: ImageBitmap? = null

    val endpoint get() = config.getEndpointServer()

    private fun loadNetworkImage(link: String, format: String): ImageBitmap {
        return imageBackground!! //TODO volontairement en erreur mais n'est pas censé etre appele par wasm

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
            println(e.stackTraceToString())
            return null
        }
    }

    private fun getUrlImageWithFileName(fileName: String) = "$endpoint/images/$fileName"

}