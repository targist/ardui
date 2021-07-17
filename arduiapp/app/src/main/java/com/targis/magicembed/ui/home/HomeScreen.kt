package com.targis.magicembed.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.targis.magicembed.R
import com.targis.magicembed.di.AppContainer
import com.targis.magicembed.extension.toProject
import com.targis.magicembed.extension.toProjectDTO
import com.targis.magicembed.room.ProjectDTO
import com.targis.magicembed.service.toStringFormat
import com.targist.magicembed.proto.Project
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@ExperimentalMaterialApi
@Composable
fun HomeScreen(
    appContainer: AppContainer,
    navigateNewProject: () -> Unit,
    navigateToEditProject: (String) -> Unit
) {

    val projectsResults = produceState(initialValue = listOf<ProjectDTO>(), producer = {
        withContext(Dispatchers.IO) {
            appContainer.appDatabase.projectDao().getAll()
                .onEach {
                    value = it
                }
                .launchIn(this)
        }
    })

    val coroutineScope = rememberCoroutineScope()
    val projects = projectsResults.value.map { it.toProject() }
    DisplayAllProjects(
        deleteProject = {
            coroutineScope.launch(Dispatchers.IO) {
                appContainer.appDatabase.projectDao().delete(it.toProjectDTO())
            }
        },
        projects = projects,
        onClickItem = navigateToEditProject,
        navigateNewProject = navigateNewProject
    )

}


@ExperimentalMaterialApi
@Composable
fun DisplayAllProjects(
    projects: List<Project>,
    onClickItem: (projectId: String) -> Unit,
    navigateNewProject: () -> Unit,
    deleteProject: (Project) -> Unit
) {


    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        // Create references for the composable to constraint
        val (lazyColumn, efb) = createRefs()


        LazyColumn(modifier = Modifier.constrainAs(lazyColumn) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }) {
            items(projects) { project ->
                DisplayProject(project = project, onClickItem, deleteProject = deleteProject)
            }
            item {
                Row(modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()) {

                }
            }
        }

        ExtendedFloatingActionButton(
            onClick = navigateNewProject,
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_add_24),
                    contentDescription = "Add new program"
                )
            },
            text = { Text(text = "New program") },
            modifier = Modifier
                .constrainAs(efb) {
                    bottom.linkTo(parent.bottom, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                }

        )
    }
}


@ExperimentalMaterialApi
@Composable
fun DisplayProject(
    project: Project,
    onClickItem: (String) -> Unit,
    deleteProject: (Project) -> Unit
) {
    Card(
        shape = RoundedCornerShape(3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(4.dp))
            .padding(4.dp),
        elevation = 4.dp,
        onClick = {
            val id = project.metadata?.id
            if (id != null) {
                onClickItem(id)
            }
        },
    )
    {
        Row(modifier = Modifier.height(intrinsicSize = IntrinsicSize.Max)) {
            Column(
                modifier = Modifier
                    .wrapContentWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(

                    painter = painterResource(id = R.drawable.arduino),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(70.dp)
                        .wrapContentWidth()
                        .fillMaxHeight(),
                    contentDescription = "Profile picture holder",
                )
            }
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    project.metadata?.name ?: "No name",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(end = 20.dp)
                )
                Text(
                    text = "created at: ${project.metadata?.createdAt?.toStringFormat() ?: "Unknown"}",
                    style = MaterialTheme.typography.body2,
                    fontStyle = FontStyle.Italic
                )
            }
            Column(
                modifier = Modifier
                    .wrapContentWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        deleteProject(project)
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                ) {
                    Text("X")
                }
            }
        }
    }
}
