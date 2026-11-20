package com.planlectura.biblia90

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.planlectura.biblia90.ui.BibliaRoot
import com.planlectura.biblia90.ui.theme.BibliaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BibliaTheme {
                BibliaRoot()
            }
        }
    }
}
