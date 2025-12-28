package de.tysw.mixes.util


import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

@Composable
fun highlightMatches(
    text: String,
    query: String,
    normalColor: Color,
    highlightColor: Color
): AnnotatedString {
    if (query.isBlank()) {
        return AnnotatedString(
            text,
            spanStyle = SpanStyle(color = normalColor)
        )
    }

    val lowerText = text.lowercase()
    val lowerQuery = query.lowercase()

    return buildAnnotatedString {
        var start = 0
        while (true) {
            val index = lowerText.indexOf(lowerQuery, start)
            if (index < 0) {
                withStyle(style = SpanStyle(color = normalColor)) {
                    append(text.substring(start))
                }
                break
            }

            // normal part
            withStyle(style = SpanStyle(color = normalColor)) {
                append(text.substring(start, index))
            }

            // highlighted match
            withStyle(
                style = SpanStyle(
                    color = normalColor,
                    background = highlightColor,
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(text.substring(index, index + query.length))
            }

            start = index + query.length
        }
    }
}

fun AnnotatedString.Builder.appendHighlighted(
    text: String,
    query: String,
    normalColor: Color,
    highlightColor: Color
) {
    if (query.isBlank()) {
        withStyle(SpanStyle(color = normalColor)) {
            append(text)
        }
        return
    }

    val lowerText = text.lowercase()
    val lowerQuery = query.lowercase()

    var start = 0

    while (true) {
        val index = lowerText.indexOf(lowerQuery, start)
        if (index < 0) {
            withStyle(SpanStyle(color = normalColor)) {
                append(text.substring(start))
            }
            break
        }

        // normal segment
        withStyle(SpanStyle(color = normalColor)) {
            append(text.substring(start, index))
        }

        // highlighted match
        withStyle(
            SpanStyle(
                color = Color.Black,
                background = highlightColor,
                fontWeight = FontWeight.SemiBold
            )
        ) {
            append(text.substring(index, index + query.length))
        }

        start = index + query.length
    }
}
