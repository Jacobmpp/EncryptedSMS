package com.esms.views.contacts.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp

@Composable
fun SearchBar(filterString: MutableState<String>) {
    val padding = 8.dp
    val focusRequester = remember { FocusRequester() }
    BasicTextField(
        value = filterString.value,
        onValueChange = {filterString.value = it},
        textStyle = TextStyle(color = MaterialTheme.colors.onBackground),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Done
        ),
        modifier = Modifier
            .focusRequester(focusRequester)
            .background(
                shape = RoundedCornerShape(padding),
                color = MaterialTheme.colors.background
            )
            .fillMaxWidth(0.85f)
            .padding(padding),
    )
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}