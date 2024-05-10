package com.esms.views.parameters.selectors

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HintPuck(hint: String) {
    if(hint.isEmpty())return
    var showHint by remember { mutableStateOf(false) }
    // IconButton to edit/get more info
    IconButton(
        onClick = { showHint = true },
        modifier = Modifier
            .height(48.dp)
            .width(80.dp)
            .offset(x = -20.dp, y = 1.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Hint Button: " + hint,
            tint = MaterialTheme.colors.onBackground
        )
    }

    if(showHint){
        AlertDialog(
            onDismissRequest = { showHint = false },
            title = { Text(text = "Parameter Info") },
            text = {
                Text(text = hint)
            },
            confirmButton = {
                Button(
                    onClick = {
                        showHint = false
                    },
                ) { Text("Done") }
            },
        )
    }
}