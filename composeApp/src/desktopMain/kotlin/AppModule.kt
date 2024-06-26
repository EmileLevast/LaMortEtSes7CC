import affichage.GraphicConstantsFullGrid
import configuration.ConfigurationImpl
import configuration.IConfiguration
import configuration.ImageDownloaderImpl
import io.ktor.serialization.Configuration
import network.ApiApp
import network.IImageDownloader
import org.koin.dsl.module

val appModule = module {
    single {ConfigurationImpl() as IConfiguration}
    single {ImageDownloaderImpl(get()) as IImageDownloader}
    single { ApiApp(get(),get()) }

    //grahic element
    single { GraphicConstantsFullGrid() }
}