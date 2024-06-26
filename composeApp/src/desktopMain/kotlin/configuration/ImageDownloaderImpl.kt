package configuration

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import network.IImageDownloader
import org.jetbrains.skia.Image
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.imageio.ImageIO

class ImageDownloaderImpl(val config: IConfiguration) : IImageDownloader{

    private var imageBackground: ImageBitmap? = null

    val endpoint get() = config.getEndpointServer()

    private fun loadNetworkImage(link: String, format: String): ImageBitmap {
        val url = URL(link)
        val connection = url.openConnection() as HttpURLConnection
        connection.connect()

        val inputStream = connection.inputStream
        val bufferedImage = ImageIO.read(inputStream)

        val stream = ByteArrayOutputStream()
        ImageIO.write(bufferedImage, format, stream)
        val byteArray = stream.toByteArray()

        return Image.makeFromEncoded(byteArray).toComposeImageBitmap()
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

    override fun downloadImageWithName(imageNameWithExtension: String): ImageBitmap {
        return try {
            downloadImageWithUrl(getUrlImageWithFileName(imageNameWithExtension))
        } catch (e: Exception) {
            useResource("UnknownImage.jpg") { loadImageBitmap(it) }
        }
    }

    fun getUrlImageWithFileName(fileName: String) = "$endpoint/images/$fileName"

}