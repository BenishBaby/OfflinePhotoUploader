package com.example.uploadkit.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.uploadkit.domain.UploadStatus

@Database(entities = [UploadEntity::class], version = 1, exportSchema = false)
@TypeConverters(UploadTypeConverters::class)
abstract class UploadDatabase : RoomDatabase() {
    abstract fun uploads(): UploadDao
}

class UploadTypeConverters {
    @androidx.room.TypeConverter
    fun fromStatus(value: UploadStatus): String = value.name

    @androidx.room.TypeConverter
    fun toStatus(value: String): UploadStatus = UploadStatus.valueOf(value)
}
