package com.esms.services

import android.content.Context
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

class Toaster(context: Context) {
    // Context is required for Toast, and LocalContext.current gives you the context of the current Composable.
    val context = context

    fun toast(message: String, long: Boolean = false){
        Toast.makeText(context, message, if(long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
    }
}