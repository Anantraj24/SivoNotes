package com.anant.sivonotes.ui.notes.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp

/**
 * VisualTransformation that renders Markdown syntax visually in the editor TextField:
 * - **bold** text is rendered with [FontWeight.Bold]
 * - *italic* text is rendered with [FontStyle.Italic]
 * - ## headings are rendered with larger font size and bold weight
 * - ~~strike~~ text is rendered with [TextDecoration.LineThrough]
 * - ☐ and ☑ checklist markers are styled distinctly
 * - • bullet markers are highlighted with primary color
 *
 * Markdown syntax tokens (** , * , ## , ~~) are rendered with a soft, subtle color
 * so the formatted content stands out while keeping exact character offsets (OffsetMapping.Identity),
 * ensuring zero cursor drift, no IME lag, and 100% stable cursor placement.
 */
class MarkdownVisualTransformation(
    private val onSurfaceColor: Color,
    private val primaryColor: Color,
    private val syntaxColor: Color = primaryColor.copy(alpha = 0.35f)
) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        if (raw.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val builder = AnnotatedString.Builder(text)

        // 1. Headings (#, ##, ###)
        val headingRegex = Regex("""(?m)^(#{1,3})\s+(.*)$""")
        for (match in headingRegex.findAll(raw)) {
            val hashGroup = match.groups[1]!!
            val contentGroup = match.groups[2]!!

            // Soft hash syntax
            builder.addStyle(
                style = SpanStyle(
                    color = syntaxColor,
                    fontWeight = FontWeight.Bold
                ),
                start = hashGroup.range.first,
                end = hashGroup.range.last + 1
            )

            val level = hashGroup.value.length
            val fontSize = when (level) {
                1 -> 24.sp
                2 -> 20.sp
                else -> 18.sp
            }
            builder.addStyle(
                style = SpanStyle(
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold,
                    color = onSurfaceColor
                ),
                start = contentGroup.range.first,
                end = contentGroup.range.last + 1
            )
        }

        // 2. Bold (**content**)
        val boldRegex = Regex("""\*\*(.*?)\*\*""")
        for (match in boldRegex.findAll(raw)) {
            val start = match.range.first
            val end = match.range.last + 1
            val innerLen = end - start - 4

            // Opening **
            builder.addStyle(
                style = SpanStyle(color = syntaxColor, fontSize = 12.sp, fontWeight = FontWeight.Normal),
                start = start,
                end = start + 2
            )

            if (innerLen > 0) {
                builder.addStyle(
                    style = SpanStyle(fontWeight = FontWeight.Bold, color = onSurfaceColor),
                    start = start + 2,
                    end = end - 2
                )
            }

            // Closing **
            builder.addStyle(
                style = SpanStyle(color = syntaxColor, fontSize = 12.sp, fontWeight = FontWeight.Normal),
                start = end - 2,
                end = end
            )
        }

        // 3. Italic (*content*) - single asterisk not surrounded by other asterisks
        val italicRegex = Regex("""(?<!\*)\*(?!\*)(.+?)(?<!\*)\*(?!\*)""")
        for (match in italicRegex.findAll(raw)) {
            val start = match.range.first
            val end = match.range.last + 1

            // Opening *
            builder.addStyle(
                style = SpanStyle(color = syntaxColor, fontSize = 12.sp),
                start = start,
                end = start + 1
            )

            builder.addStyle(
                style = SpanStyle(fontStyle = FontStyle.Italic),
                start = start + 1,
                end = end - 1
            )

            // Closing *
            builder.addStyle(
                style = SpanStyle(color = syntaxColor, fontSize = 12.sp),
                start = end - 1,
                end = end
            )
        }

        // 4. Strikethrough (~~content~~)
        val strikeRegex = Regex("""~~(.*?)~~""")
        for (match in strikeRegex.findAll(raw)) {
            val start = match.range.first
            val end = match.range.last + 1
            val innerLen = end - start - 4

            builder.addStyle(
                style = SpanStyle(color = syntaxColor, fontSize = 12.sp),
                start = start,
                end = start + 2
            )

            if (innerLen > 0) {
                builder.addStyle(
                    style = SpanStyle(textDecoration = TextDecoration.LineThrough),
                    start = start + 2,
                    end = end - 2
                )
            }

            builder.addStyle(
                style = SpanStyle(color = syntaxColor, fontSize = 12.sp),
                start = end - 2,
                end = end
            )
        }

        // 5. Checklist items (☐ and ☑)
        val checkRegex = Regex("""(?m)^([☐☑])\s+(.*)$""")
        for (match in checkRegex.findAll(raw)) {
            val symbolGroup = match.groups[1]!!
            val contentGroup = match.groups[2]!!
            val isChecked = symbolGroup.value == "☑"

            builder.addStyle(
                style = SpanStyle(
                    color = if (isChecked) primaryColor else primaryColor.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                start = symbolGroup.range.first,
                end = symbolGroup.range.last + 1
            )

            if (isChecked) {
                builder.addStyle(
                    style = SpanStyle(
                        textDecoration = TextDecoration.LineThrough,
                        color = onSurfaceColor.copy(alpha = 0.45f)
                    ),
                    start = contentGroup.range.first,
                    end = contentGroup.range.last + 1
                )
            }
        }

        // 6. Bullet items (•)
        val bulletRegex = Regex("""(?m)^([•])\s+(.*)$""")
        for (match in bulletRegex.findAll(raw)) {
            val symbolGroup = match.groups[1]!!
            builder.addStyle(
                style = SpanStyle(
                    color = primaryColor,
                    fontWeight = FontWeight.Bold
                ),
                start = symbolGroup.range.first,
                end = symbolGroup.range.last + 1
            )
        }

        return TransformedText(builder.toAnnotatedString(), OffsetMapping.Identity)
    }
}

/**
 * Strips raw markdown syntax markers (** , * , ## , ~~) for clean preview display
 * in cards (e.g. NoteCard), while preserving bold/italic/heading formatting as AnnotatedString.
 */
fun parseMarkdownForPreview(
    rawText: String,
    defaultColor: Color
): AnnotatedString {
    if (rawText.isBlank()) return AnnotatedString("")

    // Process line by line or sequentially to build clean text with spans
    return buildAnnotatedString {
        val lines = rawText.lines()
        lines.forEachIndexed { lineIndex, line ->
            var trimmedLine = line
            var isHeading = false
            if (trimmedLine.startsWith("### ")) {
                trimmedLine = trimmedLine.removePrefix("### ")
                isHeading = true
            } else if (trimmedLine.startsWith("## ")) {
                trimmedLine = trimmedLine.removePrefix("## ")
                isHeading = true
            } else if (trimmedLine.startsWith("# ")) {
                trimmedLine = trimmedLine.removePrefix("# ")
                isHeading = true
            }

            val lineStartOffset = length

            // Parse inline **bold**, *italic*, ~~strike~~
            var i = 0
            while (i < trimmedLine.length) {
                when {
                    // Bold **...**
                    trimmedLine.startsWith("**", i) -> {
                        val endIdx = trimmedLine.indexOf("**", i + 2)
                        if (endIdx != -1) {
                            val boldContent = trimmedLine.substring(i + 2, endIdx)
                            val spanStart = length
                            append(boldContent)
                            addStyle(
                                style = SpanStyle(fontWeight = FontWeight.Bold),
                                start = spanStart,
                                end = length
                            )
                            i = endIdx + 2
                        } else {
                            append(trimmedLine[i])
                            i++
                        }
                    }
                    // Italic *...*
                    trimmedLine[i] == '*' && (i == 0 || trimmedLine[i - 1] != '*') -> {
                        val endIdx = trimmedLine.indexOf('*', i + 1)
                        if (endIdx != -1 && (endIdx == trimmedLine.length - 1 || trimmedLine[endIdx + 1] != '*')) {
                            val italicContent = trimmedLine.substring(i + 1, endIdx)
                            val spanStart = length
                            append(italicContent)
                            addStyle(
                                style = SpanStyle(fontStyle = FontStyle.Italic),
                                start = spanStart,
                                end = length
                            )
                            i = endIdx + 1
                        } else {
                            append(trimmedLine[i])
                            i++
                        }
                    }
                    // Strikethrough ~~...~~
                    trimmedLine.startsWith("~~", i) -> {
                        val endIdx = trimmedLine.indexOf("~~", i + 2)
                        if (endIdx != -1) {
                            val strikeContent = trimmedLine.substring(i + 2, endIdx)
                            val spanStart = length
                            append(strikeContent)
                            addStyle(
                                style = SpanStyle(textDecoration = TextDecoration.LineThrough),
                                start = spanStart,
                                end = length
                            )
                            i = endIdx + 2
                        } else {
                            append(trimmedLine[i])
                            i++
                        }
                    }
                    else -> {
                        append(trimmedLine[i])
                        i++
                    }
                }
            }

            if (isHeading) {
                addStyle(
                    style = SpanStyle(fontWeight = FontWeight.Bold),
                    start = lineStartOffset,
                    end = length
                )
            }

            if (lineIndex < lines.size - 1) {
                append("\n")
            }
        }
    }
}
