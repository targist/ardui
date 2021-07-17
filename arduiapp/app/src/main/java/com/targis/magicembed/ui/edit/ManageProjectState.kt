package com.targis.magicembed.ui.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.targis.magicembed.di.AppContainer
import com.targis.magicembed.extension.toProjectDTO
import com.targist.magicembed.proto.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun getProjectHelper(
    projectInput: Project,
    editable: Boolean,
    appContainer: AppContainer
): ProjectHelper {
    val scope = rememberCoroutineScope()
    val (programViewState, updateProgramViewState) = remember {
        mutableStateOf(
            ProgramViewState(
                projectInput,
                editable,
                appContainer = appContainer,
                scope = scope
            )
        )
    }

    return ProjectHelper(programViewState, updateProgramViewState)
}

data class ProgramViewState(
    val project: Project,
    val editable: Boolean,
    val appContainer: AppContainer,
    val scope: CoroutineScope
)

class ProjectHelper(
    private val programViewState: ProgramViewState,
    private val updateProgramViewState: (ProgramViewState) -> Unit
) {

    var name: String
        get() {
            return programViewState.project.metadata?.name ?: ""
        }
        set(value) {
            val metadata: ProjectMetadata =
                (programViewState.project.metadata ?: ProjectMetadata()).copy(name = value)
            updateProjectLocal(programViewState.project.copy(metadata = metadata))
        }

    val project: Project
        get() = programViewState.project

    fun saveProject() {
        programViewState.scope.launch(Dispatchers.IO) {
            programViewState.appContainer.appDatabase.projectDao()
                .insertAll(programViewState.project.toProjectDTO())
        }
    }

    val setupInstructions: List<Instruction>
        get() = (programViewState.project.program?.setup?.instructions ?: emptyList())

    val loopInstructions: List<Instruction>
        get() = (programViewState.project.program?.loop?.instructions ?: emptyList())

    var editable: Boolean
        get() = programViewState.editable
        set(value) = updateEditableLocal(value)

    private fun updateProjectLocal(project: Project) {
        updateProgramViewState(programViewState.copy(project = project))
    }

    private fun updateEditableLocal(editable: Boolean) {
        updateProgramViewState(programViewState.copy(editable = editable))
    }

    fun dropSetupInstruction(instructionIndex: Int) {
        val list =
            (programViewState.project.program?.setup?.instructions?.filterIndexed { i, _ -> i != instructionIndex })
                ?: emptyList()
        val setup =
            (programViewState.project.program?.setup ?: SetupScript()).copy(instructions = list)
        val program =
            (programViewState.project.program ?: GenericArduinoProgram()).copy(setup = setup)
        updateProjectLocal(programViewState.project.copy(program = program))
    }

    fun dropLoopInstruction(instructionIndex: Int) {
        val list =
            (programViewState.project.program?.loop?.instructions?.filterIndexed { i, _ -> i != instructionIndex })
                ?: emptyList()
        val setup =
            (programViewState.project.program?.loop ?: LoopScript()).copy(instructions = list)
        val program =
            (programViewState.project.program ?: GenericArduinoProgram()).copy(loop = setup)
        updateProjectLocal(programViewState.project.copy(program = program))
    }

    fun addSetupInstruction(instruction: Instruction) {
        val list =
            (programViewState.project.program?.setup?.instructions ?: emptyList()) + instruction
        val setup =
            (programViewState.project.program?.setup ?: SetupScript()).copy(instructions = list)
        val program =
            (programViewState.project.program ?: GenericArduinoProgram()).copy(setup = setup)
        updateProjectLocal(programViewState.project.copy(program = program))
    }

    fun addLoopInstruction(instruction: Instruction) {
        val list =
            (programViewState.project.program?.loop?.instructions ?: emptyList()) + instruction
        val setup =
            (programViewState.project.program?.loop ?: LoopScript()).copy(instructions = list)
        val program =
            (programViewState.project.program ?: GenericArduinoProgram()).copy(loop = setup)
        updateProjectLocal(programViewState.project.copy(program = program))
    }

    fun clearSetupInstructions() {
        val setup = (programViewState.project.program?.setup
            ?: SetupScript()).copy(instructions = emptyList())
        val program =
            (programViewState.project.program ?: GenericArduinoProgram()).copy(setup = setup)
        updateProjectLocal(programViewState.project.copy(program = program))
    }

    fun clearLoopInstructions() {
        val setup = (programViewState.project.program?.loop
            ?: LoopScript()).copy(instructions = emptyList())
        val program =
            (programViewState.project.program ?: GenericArduinoProgram()).copy(loop = setup)
        updateProjectLocal(programViewState.project.copy(program = program))
    }

    fun updateSetupInstruction(replaceIndex: Int, replaceInstruction: Instruction) {
        val list = (programViewState.project.program?.setup?.instructions
            ?: emptyList()).mapIndexed { index, instruction -> if (replaceIndex == index) replaceInstruction else instruction }
        val setup =
            (programViewState.project.program?.setup ?: SetupScript()).copy(instructions = list)
        val program =
            (programViewState.project.program ?: GenericArduinoProgram()).copy(setup = setup)
        updateProjectLocal(programViewState.project.copy(program = program))
    }

    fun updateLoopInstruction(replaceIndex: Int, replaceInstruction: Instruction) {
        val list = (programViewState.project.program?.loop?.instructions
            ?: emptyList()).mapIndexed { index, instruction -> if (replaceIndex == index) replaceInstruction else instruction }
        val setup =
            (programViewState.project.program?.loop ?: LoopScript()).copy(instructions = list)
        val program =
            (programViewState.project.program ?: GenericArduinoProgram()).copy(loop = setup)
        updateProjectLocal(programViewState.project.copy(program = program))
    }
}
