package com.targist.magicembed.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ProjectDTO::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
}
