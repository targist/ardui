package com.targis.magicembed.ui.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.targis.magicembed.R
import com.targis.magicembed.di.AppContainer
import com.targis.magicembed.extension.toProject
import com.targis.magicembed.room.ProjectDTO
import com.targist.magicembed.proto.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.*


@Composable
fun EditProjectScreen(appContainer: AppContainer, projectId: String?) {
    if (projectId == null) {
        Text("Project ID cannot be null.")
        return
    }
    val projectResult = produceState<ProjectDTO?>(null) {
        launch(Dispatchers.IO) {
            value = appContainer.appDatabase.projectDao().findById(projectId)
        }
    }
    val project = projectResult.value
    if (project == null) {
        return
    } else {
        RunEditProgramScreen(project.toProject(), false, appContainer = appContainer)
    }
}

@Composable
fun NewProjectScreen(appContainer: AppContainer) {
    RunEditProgramScreen(
        Project(
            metadata = ProjectMetadata(
                createdAt = Instant.now(),
                id = UUID.randomUUID().toString()
            )
        ),
        true,
        appContainer = appContainer
    )
}

@Composable
fun RunEditProgramScreen(
    project: Project,
    editable: Boolean,
    appContainer: AppContainer
) = EditProgramScreen(
    helper = getProjectHelper(
        projectInput = project,
        editable = editable,
        appContainer = appContainer
    ),
    appContainer = appContainer
)

@Composable
fun EditProgramScreen(
    helper: ProjectHelper,
    appContainer: AppContainer
) {
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        TextField(
            value = helper.name,
            onValueChange = { helper.name = it },
            label = { Text("Program name") },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                textColor = Color.Black
            ),
            modifier = Modifier.fillMaxWidth(),
            readOnly = !helper.editable,
            isError = helper.name.isEmpty(),
        )



        Column(modifier = Modifier.weight(1f)) {
            InstructionEditArea(helper)
        }


        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp, alignment = Alignment.End),
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (helper.editable) {
                FloatingActionButton(
                    onClick = {
                        helper.editable = false
                        helper.saveProject()
                    }) {
                    Icon(
                        painter = painterResource(id = R.drawable.save_black_24dp),
                        contentDescription = null,
                    )
                }
            } else {
//                FloatingActionButton(onClick = {
//                    //TODO point out to run screen
//                }) {
//                    Icon(
//                        imageVector = Icons.Outlined.ArrowForward,
//                        contentDescription = null,
//                    )
//                }
                FloatingActionButton(onClick = { helper.editable = true }) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = null,
                    )
                }
//
            }
        }
    }
}

@Composable
fun InstructionEditArea(helper: ProjectHelper) {

    LazyColumn(
        Modifier
            .background(Color.LightGray)
            .padding(5.dp)
    ) {
        editScript(
            scriptName = "Setup",
            instructions = helper.setupInstructions,
            updateInstruction = { ind, inst -> helper.updateSetupInstruction(ind, inst) },
            addInstruction = { instruction -> helper.addSetupInstruction(instruction) },
            clearScript = { helper.clearSetupInstructions() },
            dropInstruction = { helper.dropSetupInstruction(it) },
            editable = helper.editable,
            numberOfAllowedInstruction = 8
        )
        editScript(
            scriptName = "Loop",
            instructions = helper.loopInstructions,
            updateInstruction = { ind, inst -> helper.updateLoopInstruction(ind, inst) },
            addInstruction = { instruction -> helper.addLoopInstruction(instruction) },
            clearScript = { helper.clearLoopInstructions() },
            dropInstruction = { helper.dropLoopInstruction(it) },
            editable = helper.editable,
            numberOfAllowedInstruction = 16
        )
    }

}

fun LazyListScope.editScript(
    scriptName: String,
    instructions: List<Instruction>,
    updateInstruction: (Int, Instruction) -> Unit,
    addInstruction: (Instruction) -> Unit,
    clearScript: () -> Unit,
    dropInstruction: (Int) -> Unit,
    editable: Boolean,
    numberOfAllowedInstruction: Int
) {
    item {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = scriptName, fontSize = 20.sp)
            if (editable) {
                Text(text = " ( ${instructions.size}/16 instructions)", fontSize = 15.sp)
            }
        }
    }
    itemsIndexed(instructions) { index, instruction ->
        ShowInstruction(
            selectedInstruction = instruction,
            index,
            updateInstruction,
            dropInstruction = dropInstruction,
            editable = editable
        )
    }
    item {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            val (showDialog, setShowDialog) = remember { mutableStateOf(false) }
            if (editable) {
                Button(
                    onClick = {
                        clearScript()
                    },
                    modifier = Modifier.padding(2.dp)
                ) {
                    Text("clear")
                }
                Button(
                    onClick = {
                        setShowDialog(true)
                    },
                    modifier = Modifier.padding(2.dp),
                    enabled = instructions.size <= numberOfAllowedInstruction
                ) {
                    Text("Add instruction")
                    DialogInstructionChoice(showDialog, setShowDialog, addInstruction)
                }
            }
        }
    }

}

@Composable
fun DialogInstructionChoice(
    showDialog: Boolean,
    setShowDialog: (Boolean) -> Unit,
    addInstruction: (Instruction) -> Unit
) {
    if (showDialog) {
        Dialog(onDismissRequest = { setShowDialog(false) }) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .padding(15.dp)
                    .clickable { },
                elevation = 10.dp
            ) {
                Column(
                    modifier = Modifier.padding(15.dp)
                ) {
                    val (expanded, setExpanded) = remember { mutableStateOf(false) }
                    val items = listOf("sleep", "analogWrite", "digitalWrite", "setPinMode")
                    val (selectedIndex, setSelectedIndex) = remember { mutableStateOf(0) }
                    Text("Select instruction")
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.LightGray),
                        onClick = { setExpanded(true) },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)

                    ) {
                        Text(text = items[selectedIndex])
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = null,
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { setExpanded(false) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items.forEachIndexed { index, s ->
                                DropdownMenuItem(
                                    onClick = {
                                        setSelectedIndex(index)
                                        setExpanded(false)
                                    },
                                ) {
                                    Text(text = s)
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            modifier = Modifier
                                .padding(15.dp),
                            onClick = {
                                when (selectedIndex) {
                                    0 -> addInstruction(Instruction(sleep = Sleep(duration = 0)))
                                    1 -> addInstruction(
                                        Instruction(
                                            analogWrite = AnalogWrite(
                                                pin = 3,
                                                value = 0
                                            )
                                        )
                                    )
                                    2 -> addInstruction(
                                        Instruction(
                                            digitalWrite = DigitalWrite(
                                                pin = 3,
                                                level = Level.LOW
                                            )
                                        )
                                    )
                                    3 -> addInstruction(
                                        Instruction(
                                            setPinMode = SetPinMode(
                                                pin = 3,
                                                mode = Mode.OUTPUT
                                            )
                                        )
                                    )
                                }
                                setShowDialog(false)
                            }
                        ) {
                            Text("Add")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShowInstruction(
    selectedInstruction: Instruction,
    instructionIndex: Int,
    updateInstruction: (Int, Instruction) -> Unit,
    dropInstruction: (Int) -> Unit,
    editable: Boolean
) {

    Card(
        shape = RoundedCornerShape(3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(4.dp))
            .padding(4.dp),
        elevation = 4.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(5.dp)
        ) {
            when {
                selectedInstruction.sleep != null -> {
                    Row {
                        Text(
                            text = "Sleep(${selectedInstruction.sleep.duration})",
                            modifier = Modifier.weight(1f)
                        )
                        if (editable) {
                            Button(
                                onClick = {
                                    dropInstruction(instructionIndex)
                                },
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
                            ) {
                                Text("X")
                            }
                        }
                    }

                    if (editable) {
                        Slider(
                            value = selectedInstruction.sleep.duration.toFloat(),
                            onValueChange = {
                                updateInstruction(
                                    instructionIndex,
                                    Instruction(sleep = Sleep(it.toInt()))
                                )
                            },
                            valueRange = 0f..1000f,
                            steps = 9
                        )

                    }
                }
                selectedInstruction.analogWrite != null -> {
                    val analogWrite = selectedInstruction.analogWrite

                    val (expanded, setExpanded) = remember { mutableStateOf(false) }
                    val analogPins = listOf(3, 5, 6, 10, 11, 12)
                    val items = analogPins.map { "Pin $it" }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "analogWrite(${analogWrite.pin},${analogWrite.value})",
                            Modifier.weight(1f)
                        )
                        if (editable) {
                            Button(
                                onClick = {
                                    dropInstruction(instructionIndex)
                                },
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
                            ) {
                                Text("X")
                            }
                        }
                    }
                    if (editable) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Slider(
                                value = analogWrite.value.toFloat(),
                                onValueChange = {
                                    updateInstruction(
                                        instructionIndex,
                                        Instruction(analogWrite = analogWrite.copy(value = it.toInt()))
                                    )
                                },
                                valueRange = 0f..255f,
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                modifier = Modifier
                                    .background(Color.Transparent),
                                onClick = { setExpanded(true) },
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),

                                ) {
                                Text(text = "Pin ${analogWrite.pin}")
                                Icon(
                                    imageVector = Icons.Filled.ArrowDropDown,
                                    contentDescription = null,
                                )
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { setExpanded(false) },
                                ) {
                                    items.forEachIndexed { index, s ->
                                        DropdownMenuItem(
                                            onClick = {
                                                updateInstruction(
                                                    instructionIndex,
                                                    Instruction(analogWrite = analogWrite.copy(pin = analogPins[index]))
                                                )
                                                setExpanded(false)
                                            },
                                        ) {
                                            Text(text = s)
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
                selectedInstruction.digitalWrite != null -> {

                    val digitalWrite = selectedInstruction.digitalWrite

                    val (expanded, setExpanded) = remember { mutableStateOf(false) }
                    val digitalPins = listOf(3, 4, 5, 6, 7, 8, 9, 10, 12, 13, 18, 19)
                    val items = digitalPins.map { "Pin $it" }

                    Row {
                        Text(
                            text = "digitalWrite(${digitalWrite.pin},${digitalWrite.level})",
                            Modifier.weight(1f)
                        )
                        if (editable) {
                            Button(
                                onClick = {
                                    dropInstruction(instructionIndex)
                                },
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
                            ) {
                                Text("X")
                            }
                        }
                    }
                    if (editable) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End,
                        ) {
                            Switch(
                                modifier = Modifier.weight(1f),
                                checked = digitalWrite.level == Level.HIGH,
                                onCheckedChange = {
                                    updateInstruction(
                                        instructionIndex,
                                        Instruction(digitalWrite = digitalWrite.copy(level = (if (it) Level.HIGH else Level.LOW)))
                                    )
                                },
                            )
                            Button(
                                modifier = Modifier
                                    .background(Color.Transparent)
                                    .weight(1f),
                                onClick = { setExpanded(true) },
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),

                                ) {
                                Text(text = "Pin ${digitalWrite.pin}")
                                Icon(
                                    imageVector = Icons.Filled.ArrowDropDown,
                                    contentDescription = null,
                                )
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { setExpanded(false) },
                                ) {
                                    items.forEachIndexed { index, s ->
                                        DropdownMenuItem(
                                            onClick = {
                                                updateInstruction(
                                                    instructionIndex,
                                                    Instruction(digitalWrite = digitalWrite.copy(pin = digitalPins[index]))
                                                )
                                                setExpanded(false)
                                            },
                                        ) {
                                            Text(text = s)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                selectedInstruction.setPinMode != null -> {
                    val setPinMode = selectedInstruction.setPinMode

                    val (expanded, setExpanded) = remember { mutableStateOf(false) }
                    val pins = listOf(3, 4, 5, 6, 7, 8, 9, 10, 12, 13, 18, 19)
                    val items = pins.map { "Pin $it" }
                    Row {
                        Text(
                            text = "pinMode(${setPinMode.pin},${setPinMode.mode})",
                            modifier = Modifier.weight(1f)
                        )
                        if (editable) {
                            Button(
                                onClick = {
                                    dropInstruction(instructionIndex)
                                },
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
                            ) {
                                Text("X")
                            }
                        }
                    }
                    if (editable) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                modifier = Modifier
                                    .background(Color.Transparent),
                                onClick = { setExpanded(true) },
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),

                                ) {
                                Text(text = "Pin ${setPinMode.pin}")
                                Icon(
                                    imageVector = Icons.Filled.ArrowDropDown,
                                    contentDescription = null,
                                )
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { setExpanded(false) },
                                ) {
                                    items.forEachIndexed { index, s ->
                                        DropdownMenuItem(
                                            onClick = {
                                                updateInstruction(
                                                    instructionIndex,
                                                    Instruction(setPinMode = setPinMode.copy(pin = pins[index]))
                                                )
                                                setExpanded(false)
                                            },
                                        ) {
                                            Text(text = s)
                                        }
                                    }
                                }
                            }
                            val modes = Mode.values()
                            val (modesExpanded, setModesExpanded) = remember { mutableStateOf(false) }
                            Button(
                                modifier = Modifier
                                    .background(Color.Transparent),
                                onClick = { setModesExpanded(true) },
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),

                                ) {
                                Text(text = setPinMode.mode.name)
                                Icon(
                                    imageVector = Icons.Filled.ArrowDropDown,
                                    contentDescription = null,
                                )
                                DropdownMenu(
                                    expanded = modesExpanded,
                                    onDismissRequest = { setModesExpanded(false) },
                                ) {
                                    modes.forEachIndexed { index, s ->
                                        DropdownMenuItem(
                                            onClick = {
                                                updateInstruction(
                                                    instructionIndex,
                                                    Instruction(setPinMode = setPinMode.copy(mode = modes[index]))
                                                )
                                                setModesExpanded(false)
                                            },
                                        ) {
                                            Text(text = s.name)
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
                else -> Text(text = "Unknown instruction")
            }
        }
    }
}
