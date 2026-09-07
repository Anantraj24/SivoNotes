package com.anant.sivonotes.ui.notes.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
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
            if (contentGroup.value.isNotEmpty()) {
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

        // 3. Italic (*content*) - single asterisk, not flanked by whitespace or extra asterisks
        val italicRegex = Regex("""(?<!\*)\*(?!\s|\*)([^\*\n]+?)(?<!\s|\*)\*(?!\*)""")
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

            if (isChecked && contentGroup.value.isNotEmpty()) {
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

/** Formatting modes supported by the toolbar */
enum class MarkdownFormatType {
    BOLD,
    ITALIC,
    HEADING,
    BULLET,
    CHECKLIST
}

/**
 * Pure formatting operations for note content.
 * Completely deterministic and independent of UI/coroutines, making it easily testable.
 */
object MarkdownFormatter {

    fun applyFormatting(current: TextFieldValue, type: MarkdownFormatType): TextFieldValue {
        val text = current.text
        val selMin = current.selection.min.coerceIn(0, text.length)
        val selMax = current.selection.max.coerceIn(0, text.length)
        val hasSelection = selMin != selMax

        return when (type) {
            MarkdownFormatType.BOLD -> applyInlineFormat(text, selMin, selMax, hasSelection, "**")
            MarkdownFormatType.ITALIC -> applyInlineFormat(text, selMin, selMax, hasSelection, "*")
            MarkdownFormatType.HEADING -> toggleLineHeading(text, selMin)
            MarkdownFormatType.BULLET -> toggleLinePrefix(text, selMin, "• ")
            MarkdownFormatType.CHECKLIST -> toggleChecklist(text, selMin)
        }
    }

    private fun applyInlineFormat(
        text: String,
        selMin: Int,
        selMax: Int,
        hasSelection: Boolean,
        marker: String
    ): TextFieldValue {
        val markerLen = marker.length

        if (hasSelection) {
            val selected = text.substring(selMin, selMax)
            val isDirectlyWrapped = if (marker == "*") {
                selected.startsWith("*") && selected.endsWith("*") &&
                    !selected.startsWith("**") && !selected.endsWith("**") &&
                    selected.length >= 2
            } else {
                selected.startsWith(marker) && selected.endsWith(marker) && selected.length >= markerLen * 2
            }

            if (isDirectlyWrapped) {
                val unwrapped = selected.substring(markerLen, selected.length - markerLen)
                val newText = text.substring(0, selMin) + unwrapped + text.substring(selMax)
                return TextFieldValue(newText, TextRange(selMin, selMin + unwrapped.length))
            }

            if (isWrappedWith(text, selMin, selMax, marker)) {
                val newText = text.substring(0, selMin - markerLen) + selected + text.substring(selMax + markerLen)
                val newStart = selMin - markerLen
                return TextFieldValue(newText, TextRange(newStart, newStart + selected.length))
            }

            val newText = text.substring(0, selMin) + marker + selected + marker + text.substring(selMax)
            val newCursor = selMax + markerLen * 2
            return TextFieldValue(newText, TextRange(newCursor))
        } else {
            val cursorPos = selMin
            val (wordStart, wordEnd) = findWordBounds(text, cursorPos)
            if (wordStart < wordEnd) {
                val word = text.substring(wordStart, wordEnd)
                if (isWrappedWith(text, wordStart, wordEnd, marker)) {
                    val newText = text.substring(0, wordStart - markerLen) + word + text.substring(wordEnd + markerLen)
                    val newCursor = (cursorPos - markerLen).coerceIn(wordStart - markerLen, wordStart - markerLen + word.length)
                    return TextFieldValue(newText, TextRange(newCursor))
                } else {
                    val newText = text.substring(0, wordStart) + marker + word + marker + text.substring(wordEnd)
                    val newCursor = wordEnd + markerLen * 2
                    return TextFieldValue(newText, TextRange(newCursor))
                }
            } else {
                val newText = text.substring(0, cursorPos) + marker + marker + text.substring(cursorPos)
                val newCursor = cursorPos + markerLen
                return TextFieldValue(newText, TextRange(newCursor))
            }
        }
    }

    private fun isWrappedWith(text: String, start: Int, end: Int, marker: String): Boolean {
        val markerLen = marker.length
        if (start < markerLen || end + markerLen > text.length) return false
        val before = text.substring(start - markerLen, start)
        val after = text.substring(end, end + markerLen)
        if (before != marker || after != marker) return false

        if (marker == "*") {
            val hasExtraBefore = start - markerLen > 0 && text[start - markerLen - 1] == '*'
            val hasExtraAfter = end + markerLen < text.length && text[end + markerLen] == '*'
            if (hasExtraBefore || hasExtraAfter) return false
        }
        return true
    }

    private fun findWordBounds(text: String, cursorPos: Int): Pair<Int, Int> {
        if (text.isEmpty()) return Pair(0, 0)

        val checkPos = when {
            cursorPos > 0 && !text[cursorPos - 1].isWhitespace() && text[cursorPos - 1] != '*' && text[cursorPos - 1] != '#' -> cursorPos - 1
            cursorPos < text.length && !text[cursorPos].isWhitespace() && text[cursorPos] != '*' && text[cursorPos] != '#' -> cursorPos
            else -> return Pair(cursorPos, cursorPos)
        }

        var start = checkPos
        while (start > 0 && !text[start - 1].isWhitespace() && text[start - 1] != '*' && text[start - 1] != '#' && text[start - 1] != '~') {
            start--
        }

        var end = checkPos + 1
        while (end < text.length && !text[end].isWhitespace() && text[end] != '*' && text[end] != '#' && text[end] != '~') {
            end++
        }

        return Pair(start, end)
    }

    private fun toggleLineHeading(text: String, cursorPos: Int): TextFieldValue {
        val lineStart = text.lastIndexOf('\n', (cursorPos - 1).coerceAtLeast(0)).let {
            if (it == -1) 0 else it + 1
        }
        val lineEnd = text.indexOf('\n', cursorPos).let {
            if (it == -1) text.length else it
        }
        val line = text.substring(lineStart, lineEnd)

        val (newLine, delta) = when {
            line.startsWith("### ") -> Pair(line.removePrefix("### "), -4)
            line.startsWith("## ") -> Pair("### " + line.removePrefix("## "), 1)
            line.startsWith("# ") -> Pair("## " + line.removePrefix("# "), 1)
            else -> {
                val cleanLine = line.removePrefix("• ").removePrefix("- ").removePrefix("☐ ").removePrefix("☑ ")
                val diff = cleanLine.length - line.length
                Pair("## $cleanLine", 3 + diff)
            }
        }

        val newText = text.substring(0, lineStart) + newLine + text.substring(lineEnd)
        val newCursor = (cursorPos + delta).coerceIn(lineStart, lineStart + newLine.length)
        return TextFieldValue(newText, TextRange(newCursor))
    }

    private fun toggleLinePrefix(text: String, cursorPos: Int, prefix: String): TextFieldValue {
        val lineStart = text.lastIndexOf('\n', (cursorPos - 1).coerceAtLeast(0)).let {
            if (it == -1) 0 else it + 1
        }
        val lineEnd = text.indexOf('\n', cursorPos).let {
            if (it == -1) text.length else it
        }
        val line = text.substring(lineStart, lineEnd)

        val (newLine, delta) = if (line.startsWith(prefix)) {
            Pair(line.removePrefix(prefix), -prefix.length)
        } else {
            val cleanLine = line.removePrefix("• ").removePrefix("- ").removePrefix("☐ ").removePrefix("☑ ").removePrefix("## ").removePrefix("### ").removePrefix("# ")
            val diff = cleanLine.length - line.length
            Pair(prefix + cleanLine, prefix.length + diff)
        }

        val newText = text.substring(0, lineStart) + newLine + text.substring(lineEnd)
        val newCursor = (cursorPos + delta).coerceIn(lineStart, lineStart + newLine.length)
        return TextFieldValue(newText, TextRange(newCursor))
    }

    private fun toggleChecklist(text: String, cursorPos: Int): TextFieldValue {
        val lineStart = text.lastIndexOf('\n', (cursorPos - 1).coerceAtLeast(0)).let {
            if (it == -1) 0 else it + 1
        }
        val lineEnd = text.indexOf('\n', cursorPos).let {
            if (it == -1) text.length else it
        }
        val line = text.substring(lineStart, lineEnd)

        val (newLine, delta) = when {
            line.startsWith("☐ ") -> Pair("☑ " + line.removePrefix("☐ "), 0)
            line.startsWith("☑ ") -> Pair(line.removePrefix("☑ "), -2)
            else -> {
                val cleanLine = line.removePrefix("• ").removePrefix("- ").removePrefix("## ").removePrefix("### ").removePrefix("# ")
                val diff = cleanLine.length - line.length
                Pair("☐ " + cleanLine, 2 + diff)
            }
        }

        val newText = text.substring(0, lineStart) + newLine + text.substring(lineEnd)
        val newCursor = (cursorPos + delta).coerceIn(lineStart, lineStart + newLine.length)
        return TextFieldValue(newText, TextRange(newCursor))
    }
}
