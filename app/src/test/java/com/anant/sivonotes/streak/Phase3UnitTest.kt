package com.anant.sivonotes.streak

import com.anant.sivonotes.data.local.entity.TodoEntity
import com.anant.sivonotes.domain.streak.StreakEngine
import com.anant.sivonotes.notification.ReminderReceiver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class Phase3UnitTest {

    @Test
    fun testStreakCalculationConsecutiveDays() {
        val now = System.currentTimeMillis()
        val oneDayMillis = 24 * 60 * 60 * 1000L

        val todo1 = TodoEntity(id = 1, title = "T1", isCompleted = true, completedAt = now)
        val todo2 = TodoEntity(id = 2, title = "T2", isCompleted = true, completedAt = now - oneDayMillis)
        val todo3 = TodoEntity(id = 3, title = "T3", isCompleted = true, completedAt = now - 2 * oneDayMillis)

        val stats = StreakEngine.calculateStats(listOf(todo1, todo2, todo3))
        assertEquals(3, stats.currentStreak)
        assertEquals(3, stats.bestStreak)
        assertEquals(3, stats.totalCompleted)
    }

    @Test
    fun testStreakCalculationZeroIfNoCompleted() {
        val todo1 = TodoEntity(id = 1, title = "T1", isCompleted = false)
        val stats = StreakEngine.calculateStats(listOf(todo1))
        assertEquals(0, stats.currentStreak)
        assertEquals(0, stats.bestStreak)
    }

    @Test
    fun testNextOccurrenceDaily() {
        val cal = Calendar.getInstance()
        val currentMillis = cal.timeInMillis
        val nextDaily = ReminderReceiver.calculateNextOccurrence(currentMillis, "DAILY")
        val expectedMin = currentMillis + 23 * 60 * 60 * 1000L
        val expectedMax = currentMillis + 25 * 60 * 60 * 1000L

        assertTrue(nextDaily in expectedMin..expectedMax)
    }
}
