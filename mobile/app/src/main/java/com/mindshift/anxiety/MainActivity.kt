package com.mindshift.anxiety

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.mindshift.anxiety.ui.navigation.AppNavigation
import com.mindshift.anxiety.ui.theme.MindShiftTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MindShiftTheme {
                AppNavigation()
            }
        }
    }
}
