package com.targist.ardui.extension

import com.targist.ardui.proto.Project
import com.targist.ardui.proto.ProjectMetadata
import com.targist.ardui.room.ProjectDTO

import java.util.*

fun Project.toProjectDTO(): ProjectDTO = ProjectDTO(data = this.encode(), id = this.metadata?.id?:UUID.randomUUID().toString())

fun ProjectDTO.toProject(): Project {
    val project = Project.ADAPTER.decode(this.data ?: byteArrayOf())
    return project.copy(metadata = (project.metadata ?: ProjectMetadata()).copy(id = this.id))
}
