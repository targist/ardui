package com.targis.magicembed.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class ProjectDTO(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val data: ByteArray? = null
)

