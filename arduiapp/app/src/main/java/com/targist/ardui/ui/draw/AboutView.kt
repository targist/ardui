package com.targist.ardui.ui.draw

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
fun AboutView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .padding(15.dp)
            .clickable { },
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = "ArdUI is an application that allows programming Arduino from a mobile terminal.\n" +
                    "\n" )

            Text(text = "From targist.com\n", modifier = Modifier.clickable {
                //TODO add clickable link
            })
            Text(text = "Github https://github.com/targist/ardui ", modifier = Modifier.clickable {
                //TODO add clickable link
            })


        }
    }
}