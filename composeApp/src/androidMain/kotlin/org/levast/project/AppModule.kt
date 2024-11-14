package org.levast.project

import configuration.ConfigurationImpl
import configuration.GraphicConstantsFullGrid
import configuration.IConfiguration
import network.ApiApp
import network.IImageDownloader
import org.koin.dsl.module
import org.levast.project.configuration.ImageDownloaderImpl

val appModule = module {
    single { ConfigurationImpl() as IConfiguration}
    single { ImageDownloaderImpl(get()) as IImageDownloader}
    single { ApiApp(get(),get()) }

    //grahic element
    single { GraphicConstantsFullGrid() }
}