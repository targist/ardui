package com.targis.magicembed.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {

    @Query("SELECT * FROM projectdto")
    fun getAll(): Flow<List<ProjectDTO>>

    @Query("SELECT * FROM projectdto WHERE id IN (:projectId)")
    suspend fun loadAllByIds(projectId: Array<String>): List<ProjectDTO>

    @Query("SELECT * FROM projectdto WHERE id = (:id)")
    suspend fun findById(id: String): ProjectDTO?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg projectDTO: ProjectDTO)

    @Delete
    suspend fun delete(projectDTO: ProjectDTO)

}
