package configuration

import network.ApiApp
import org.levast.project.configuration.ImageDownloaderImpl

val graphicConstantsFullGrid = GraphicConstantsFullGrid()
val configurationImpl = ConfigurationImpl()
val imageDownloaderImpl = ImageDownloaderImpl(configurationImpl)
val apiApp = ApiApp(configurationImpl, imageDownloaderImpl)
