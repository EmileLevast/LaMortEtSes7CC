package org.levast.project

import App
import AppMobile
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.compose.AppTheme
import org.koin.compose.KoinContext
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
            AppMobile()
        }
    }
}