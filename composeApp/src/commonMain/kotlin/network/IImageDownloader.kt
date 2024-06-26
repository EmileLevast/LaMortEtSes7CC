package network

import androidx.compose.ui.graphics.ImageBitmap

interface IImageDownloader {
    fun downloadBackgroundImage(urlImage: String): ImageBitmap
    fun downloadImageWithName(imageNameWithExtension: String): ImageBitmap
}