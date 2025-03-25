package configuration

import androidx.compose.runtime.Composable
import network.ApiApp
import network.IImageDownloader
import org.koin.compose.koinInject

@Composable
actual fun getConfiguration(): IConfiguration {
    return koinInject<IConfiguration>()
}

@Composable
actual fun getApiApp(): ApiApp {
    return koinInject<ApiApp>()

}

@Composable
actual fun getImageDownloader(): IImageDownloader {
    return koinInject<IImageDownloader>()
}

@Composable
actual fun getGraphicConstants(): GraphicConstantsFullGrid {
    return koinInject<GraphicConstantsFullGrid>()
}