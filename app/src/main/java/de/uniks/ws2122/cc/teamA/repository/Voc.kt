package de.uniks.ws2122.cc.teamA.repository

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Voc(
    @PrimaryKey(autoGenerate = true) var id: Long
)