package configuration

import androidx.compose.runtime.Composable
import network.ApiApp
import network.IImageDownloader

@Composable
expect fun getConfiguration(): IConfiguration

@Composable
expect fun getApiApp(): ApiApp

@Composable
expect fun getImageDownloader(): IImageDownloader

@Composable
expect fun getGraphicConstants(): GraphicConstantsFullGrid