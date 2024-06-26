import affichage.GraphicConstantsFullGrid
import configuration.Configuration
import network.ApiApp
import org.koin.dsl.module

val appModule = module {
    single {Configuration()}
    single { ApiApp(get()) }

    //grahic element
    single { GraphicConstantsFullGrid() }
}