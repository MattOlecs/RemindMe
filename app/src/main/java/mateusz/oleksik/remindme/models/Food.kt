package mateusz.oleksik.remindme.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Food(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "Name") val name: String?,
    @ColumnInfo(name = "ExpirationDate") val expirationDate: Long,
)