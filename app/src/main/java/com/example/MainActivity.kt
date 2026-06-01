package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.room.Room
import com.example.data.EMRDatabase
import com.example.data.EMRRepository
import com.example.ui.EMRAppContent
import com.example.ui.EMRViewModel
import com.example.ui.EMRViewModelFactory
import com.example.ui.theme.EMRTheme

class MainActivity : ComponentActivity() {

    // Lazy instantiation of Offline-first Room DB
    private val database: EMRDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            EMRDatabase::class.java,
            "asha_emr_secure.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    private val repository: EMRRepository by lazy {
        EMRRepository(database.emrDao())
    }

    // Creating VM using standard factory provider
    private val emrViewModel: EMRViewModel by viewModels {
        EMRViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EMRTheme {
                EMRAppContent(viewModel = emrViewModel)
            }
        }
    }
}
