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
                text = """
Before starting:

- Get the library Ardui.zip from
  https://github.com/targist/ardui/blob/main/arduino-library/generated/c/Ardui.zip
- Upload GenericProgram.ino from
  https://github.com/targist/ardui/blob/main/arduino-library/GenericProgram/GenericProgram.ino
  to your Arduino board.

Create a new program by defining the name, setup and loop instructions. once
done save the program and connect your mobile to an Arduino board via Serial.
Finally go to the Serial view in the app and upload your program.

Enjoy!
            """
            )

        }
    }
}