package com.targis.magicembed.extension

import com.targis.magicembed.room.ProjectDTO
import com.targist.magicembed.proto.Project
import com.targist.magicembed.proto.ProjectMetadata
import java.util.*

fun Project.toProjectDTO(): ProjectDTO = ProjectDTO(data = this.encode(), id = this.metadata?.id?:UUID.randomUUID().toString())

fun ProjectDTO.toProject(): Project {
    val project = Project.ADAPTER.decode(this.data ?: byteArrayOf())
    return project.copy(metadata = (project.metadata ?: ProjectMetadata()).copy(id = this.id))
}
