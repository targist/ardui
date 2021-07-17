package com.targist.magicembed.ui.draw

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun HelpView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .padding(15.dp)
            .clickable { },
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = """|
                |Use the Add instruction button for adding instructions to your
                |Setup and Loop functions.
                |When done upload it to your Arduino board
                |Don't forget to specify the Serial port
                |
                |Enjoy!
            """.trimMargin()
            )

        }
    }
}