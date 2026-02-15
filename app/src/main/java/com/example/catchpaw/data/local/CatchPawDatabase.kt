package com.example.catchpaw.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [GameScoreEntity::class], version = 1, exportSchema = false)
abstract class CatchPawDatabase : RoomDatabase() {
    abstract fun gameScoreDao(): GameScoreDao
}
