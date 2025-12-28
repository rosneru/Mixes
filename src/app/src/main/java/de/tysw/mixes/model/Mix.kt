package de.tysw.mixes.model

import java.time.YearMonth
import java.util.UUID

data class Track(
    val number: Int,
    val artist: String,
    val title: String,
    val isLiked: Boolean
)

data class Mix(
    val created: YearMonth, // Instead of string, much nicer typed
    val title: String,
    val tracks: List<Track>,
    val rating: Int,
    val id: UUID = UUID.randomUUID()
)
