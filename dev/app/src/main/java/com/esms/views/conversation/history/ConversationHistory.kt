package com.esms.views.conversation.history

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.esms.models.SMSMessage
import com.esms.models.parseDate
import com.esms.services.SmsService


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ConversationHistory() {
    val context = LocalContext.current
    val allMessages = remember { mutableStateMapOf<String, List<SMSMessage>>()}
    val smsService = SmsService(context)
    LaunchedEffect(key1 = Unit) {
        val messages =
            smsService.readMessages(type = "inbox") +
            smsService.readMessages(type = "sent")
        allMessages += messages.sortedBy { it.date }.groupBy { it.sender }
    }

    val scrollState = rememberLazyListState()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = scrollState
    ) {
        allMessages.forEach { (sender, messages) ->
            stickyHeader(key = sender) {
                Text(
                    text = sender,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            messages.groupBy { it.date.parseDate().split(" ").first() }
                .forEach { (date, smsMessages) ->
                    item {
                        Text(
                            text = date,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    items(
                        items = smsMessages,
                        key = {it.date}
                    ) {
                        MessageBox(
                            content = it.body,
                            time = it.date,
                            recieved = false
                        )
                    }
                }
        }
    }
}

@Preview
@Composable
fun ConversationHistoryPreview() {
    ConversationHistory()
}