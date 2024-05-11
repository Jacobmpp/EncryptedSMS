package com.esms.views.contacts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.esms.models.LocalParameters
import com.esms.models.PhoneContact
import com.esms.views.contacts.pic.ProfilePicture

@Composable
fun ContactBox(contact: PhoneContact, showPriority: Boolean = false) {
    val params = LocalParameters.current
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(50.dp)
    ) {
        // pfp
        ProfilePicture(
            name = params.getNicknameForNumber(contact.number, contact.name),
            number = contact.number.hashCode(),
            url = contact.pfp
        )
        
        Spacer(modifier = Modifier.padding(horizontal = 5.dp))
        
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight()
        ) {
            Row(){
            // Name
                Text(
                    text = params.getNicknameForNumber(contact.number, contact.name),
                    color = MaterialTheme.colors.onSurface
                )
                val priority = if(showPriority) params.getSortingPriorityForNumber(contact.number) else 0f
                if(priority != 0f){
                    val simplePriority = if (priority % 1f == 0f) priority.toInt() else priority
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Contact has a sorting priority of $simplePriority",
                        tint = MaterialTheme.colors.onSurface.copy(alpha = 0.3f),
                        modifier = Modifier.offset(x = 4.dp, y = -1.dp)
                    )
                    Text(
                        text = " $simplePriority",
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
                    )
                }
            }
            // Number
            Text(
                text = contact.number,
                color = MaterialTheme.colors.onSurface
            )
        }

    }
}