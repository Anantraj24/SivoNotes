package com.anant.sivonotes.ui.notes

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.anant.sivonotes.ui.notes.components.MarkdownFormatType
import com.anant.sivonotes.ui.notes.components.MarkdownFormatter
import com.anant.sivonotes.ui.notes.components.MarkdownVisualTransformation
import com.anant.sivonotes.ui.notes.components.parseMarkdownForPreview
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MarkdownFormattingTest {

    private val primaryColor = Color(0xFF6C5CE7)
    private val onSurfaceColor = Color(0xFF2D3436)
    private lateinit var transformation: MarkdownVisualTransformation

    @Before
    fun setup() {
        transformation = MarkdownVisualTransformation(
            onSurfaceColor = onSurfaceColor,
            primaryColor = primaryColor
        )
    }

    // ── 1. MarkdownVisualTransformation Tests ─────────────────────────────────

    @Test
    fun testBoldVisualTransformation() {
        val input = AnnotatedString("Hello **boalt** world")
        val transformed = transformation.filter(input)

        // Character indices must remain strictly identical
        assertEquals(input.text, transformed.text.text)

        // The text "boalt" (index 8 to 13) must be FontWeight.Bold
        val boldSpans = transformed.text.spanStyles.filter { it.item.fontWeight == FontWeight.Bold }
        assertTrue("Expected bold span on 'boalt'", boldSpans.any { it.start == 8 && it.end == 13 })
    }

    @Test
    fun testItalicVisualTransformation() {
        val input = AnnotatedString("This is *italic* text")
        val transformed = transformation.filter(input)

        assertEquals(input.text, transformed.text.text)

        val italicSpans = transformed.text.spanStyles.filter { it.item.fontStyle == FontStyle.Italic }
        assertTrue("Expected italic span on 'italic'", italicSpans.any { it.start == 9 && it.end == 15 })
    }

    @Test
    fun testMathExpressionNotItalicized() {
        val input = AnnotatedString("Calculate 5 * 3 * 2 = 30")
        val transformed = transformation.filter(input)

        val italicSpans = transformed.text.spanStyles.filter { it.item.fontStyle == FontStyle.Italic }
        assertTrue("Math formulas with spaces around * should not be italicized", italicSpans.isEmpty())
    }

    @Test
    fun testHeadingVisualTransformation() {
        val input = AnnotatedString("## Project Notes\nBody text here")
        val transformed = transformation.filter(input)

        assertEquals(input.text, transformed.text.text)

        val headingSpans = transformed.text.spanStyles.filter {
            it.item.fontSize == 20.sp && it.item.fontWeight == FontWeight.Bold
        }
        assertTrue("Expected 20.sp bold heading span", headingSpans.any { it.start == 3 && it.end == 16 })
    }

    @Test
    fun testChecklistAndBulletVisualTransformation() {
        val input = AnnotatedString("☐ Pending task\n☑ Completed task\n• Bullet item")
        val transformed = transformation.filter(input)

        assertEquals(input.text, transformed.text.text)

        // Completed task content should have strikethrough
        val strikeSpans = transformed.text.spanStyles.filter {
            it.item.textDecoration == TextDecoration.LineThrough
        }
        assertTrue("Completed checklist item should have strikethrough", strikeSpans.isNotEmpty())
    }

    @Test
    fun testEmptyAndBoundaryInputs() {
        val empty = AnnotatedString("")
        val transformedEmpty = transformation.filter(empty)
        assertEquals("", transformedEmpty.text.text)

        val emptyBold = AnnotatedString("****")
        val transformedBold = transformation.filter(emptyBold)
        assertEquals("****", transformedBold.text.text)

        val loneAsterisk = AnnotatedString("Just * lone star")
        val transformedLone = transformation.filter(loneAsterisk)
        assertEquals("Just * lone star", transformedLone.text.text)
    }

    // ── 2. parseMarkdownForPreview Tests ──────────────────────────────────────

    @Test
    fun testParseMarkdownForPreview() {
        val input = "## Meeting\nDiscussed **Q3 Goals** and *budget*"
        val preview = parseMarkdownForPreview(input, onSurfaceColor)

        // Markdown markers should be stripped in the preview card
        assertEquals("Meeting\nDiscussed Q3 Goals and budget", preview.text)

        // "Meeting" should be bold
        val meetingBold = preview.spanStyles.filter { it.item.fontWeight == FontWeight.Bold }
        assertTrue("Expected bold on 'Meeting' and 'Q3 Goals'", meetingBold.size >= 2)

        // "budget" should be italic
        val budgetItalic = preview.spanStyles.filter { it.item.fontStyle == FontStyle.Italic }
        assertTrue("Expected italic on 'budget'", budgetItalic.isNotEmpty())
    }

    // ── 3. MarkdownFormatter Formatting Tests ─────────────────────────────────

    @Test
    fun testBoldOnSelectionAndToggle() {
        // 1. Wrap selected text "boalt"
        val initial = TextFieldValue("Hello boalt world", TextRange(6, 11))
        val formatted = MarkdownFormatter.applyFormatting(initial, MarkdownFormatType.BOLD)
        assertEquals("Hello **boalt** world", formatted.text)

        // 2. Toggle OFF when selection includes markers
        val selectWrapped = TextFieldValue("Hello **boalt** world", TextRange(6, 15))
        val toggled = MarkdownFormatter.applyFormatting(selectWrapped, MarkdownFormatType.BOLD)
        assertEquals("Hello boalt world", toggled.text)
    }

    @Test
    fun testBoldOnWordUnderCursorWithoutSelection() {
        // Cursor immediately after "boalt" (index 11) with NO selection
        val initial = TextFieldValue("Hello boalt world", TextRange(11))
        val formatted = MarkdownFormatter.applyFormatting(initial, MarkdownFormatType.BOLD)
        assertEquals("Hello **boalt** world", formatted.text)

        // Cursor inside "boalt" (index 10) on already-bold word toggles OFF
        val insideBold = TextFieldValue("Hello **boalt** world", TextRange(10))
        val toggled = MarkdownFormatter.applyFormatting(insideBold, MarkdownFormatType.BOLD)
        assertEquals("Hello boalt world", toggled.text)
    }

    @Test
    fun testHeadingCycle() {
        val initial = TextFieldValue("Meeting Notes", TextRange(13))

        // First click: adds ##
        val h2 = MarkdownFormatter.applyFormatting(initial, MarkdownFormatType.HEADING)
        assertEquals("## Meeting Notes", h2.text)

        // Second click: changes ## to ###
        val h3 = MarkdownFormatter.applyFormatting(h2, MarkdownFormatType.HEADING)
        assertEquals("### Meeting Notes", h3.text)

        // Third click: removes ### (cycles back to plain text)
        val plain = MarkdownFormatter.applyFormatting(h3, MarkdownFormatType.HEADING)
        assertEquals("Meeting Notes", plain.text)
    }

    @Test
    fun testBulletToggle() {
        val initial = TextFieldValue("Milk", TextRange(4))

        val bulleted = MarkdownFormatter.applyFormatting(initial, MarkdownFormatType.BULLET)
        assertEquals("• Milk", bulleted.text)

        val toggled = MarkdownFormatter.applyFormatting(bulleted, MarkdownFormatType.BULLET)
        assertEquals("Milk", toggled.text)
    }

    @Test
    fun testChecklistCycle() {
        val initial = TextFieldValue("Finish report", TextRange(13))

        // 1. Add ☐
        val step1 = MarkdownFormatter.applyFormatting(initial, MarkdownFormatType.CHECKLIST)
        assertEquals("☐ Finish report", step1.text)

        // 2. Cycle to ☑
        val step2 = MarkdownFormatter.applyFormatting(step1, MarkdownFormatType.CHECKLIST)
        assertEquals("☑ Finish report", step2.text)

        // 3. Remove checklist marker
        val step3 = MarkdownFormatter.applyFormatting(step2, MarkdownFormatType.CHECKLIST)
        assertEquals("Finish report", step3.text)
    }

    @Test
    fun testItalicDoesNotCorruptBold() {
        // Cursor inside "boalt" of "**boalt**"
        val boldText = TextFieldValue("**boalt**", TextRange(4))
        val italicized = MarkdownFormatter.applyFormatting(boldText, MarkdownFormatType.ITALIC)

        // Should wrap as ***boalt*** (bold + italic), NOT strip an asterisk!
        assertEquals("***boalt***", italicized.text)
    }
}
