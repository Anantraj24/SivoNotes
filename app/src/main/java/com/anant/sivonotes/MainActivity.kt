package com.anant.sivonotes

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import com.anant.sivonotes.ui.main.MainAppScaffold
import com.anant.sivonotes.ui.theme.SivoNotesTheme

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SivoNotesTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainAppScaffold()
                }
            }
        }
    }
}