
package com.aikodelvalle.gameshelf

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.aikodelvalle.gameshelf.presentation.navigation.AppNavigation
import com.aikodelvalle.gameshelf.ui.theme.GameShelfTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GameShelfTheme {
                AppNavigation()
            }
        }
    }
}
