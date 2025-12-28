package de.tysw.mixes.util

import java.time.YearMonth
import de.tysw.mixes.model.Mix
import de.tysw.mixes.model.Track

object MixParser {

    fun parse(input: String): List<Mix> {
        val mixes = mutableListOf<Mix>()
        var created: YearMonth? = null
        var title: String? = null
        var rating = 0
        val currentTracks = mutableListOf<Track>()

        fun finishMix() {
            if (created != null && title != null) {
                mixes += Mix(
                    created = created!!,
                    title = title!!,
                    rating = rating,
                    tracks = currentTracks.toList()
                )
            }
            created = null
            title = null
            rating = 0
            currentTracks.clear()
        }

        /**
         * Regular expression for the header:
         * 1 = yyyy-mm
         * 2 = Titel ohne Rating
         * 3 = Rating (optional)
         *
         * Examples:
         * 2020-03 FooBar (+)
         * 2020-03 FooBar (++)
         * 2020-03 FooBar
         */
        val headerRegex = Regex(
            """^(\d{4}-\d{2})\s+(.+?)(?:\s+\(([-+]+)\))?$"""
        )

        /**
         * Regular expression for the track:
         * optional * before the number
         * 1 = optional asterisk
         * 2 = Number
         * 3 = Artist
         * 4 = Title
         */
        val trackRegex = Regex(
            """^\s*(\*)?\s*(\d+)\.\s+(.+?)\s*-\s*(.+)$"""
        )

        input.lines().forEach { raw ->
            val line = raw.trim()
            if (line.isBlank()) return@forEach  // do not leave parse, but just
                                                // this `forEach` iteration, so
                                                // continue with the next line.
                                                // (Because no `continue` is not
                                                // available in lambdas)

            // HEADER?
            headerRegex.matchEntire(line)?.let { m ->
                finishMix()

                created = YearMonth.parse(m.groupValues[1])
                title = m.groupValues[2]

                val ratingChars = m.groupValues[3]
                rating = if (ratingChars.isNotBlank()) {
                    if (ratingChars.all { it == '+' }) ratingChars.length
                    else if (ratingChars.all { it == '-' }) -ratingChars.length
                    else 0
                } else 0

                return@forEach
            }

            // TRACK?
            trackRegex.matchEntire(line)?.let { m ->
                val isLiked = m.groupValues[1] == "*"
                currentTracks += Track(
                    number = m.groupValues[2].toInt(),
                    artist = m.groupValues[3],
                    title = m.groupValues[4],
                    isLiked = isLiked
                )
            }
        }

        finishMix()
        return mixes
    }
}
