package org.levast.project

import App
import AppMobile
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.compose.AppTheme
import configuration.ConfigurationImpl
import configuration.IConfiguration
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import org.koin.core.context.startKoin



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppAndroid()
        }
    }
}

@Composable
fun AppAndroid(){

    startKoin {
        modules(appModule)
    }

    KoinContext {
        AppTheme {

            //Redirige vers le code de commonMain
            AppMobile()
        }
    }
}