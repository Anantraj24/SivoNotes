package com.anant.sivonotes.domain.streak

import com.anant.sivonotes.data.local.entity.FocusSessionEntity
import com.anant.sivonotes.data.local.entity.TodoEntity
import java.util.Calendar
import java.util.concurrent.TimeUnit

data class StreakStats(
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val totalCompleted: Int = 0,
    val weeklyCompleted: Int = 0,
    val weeklyTotal: Int = 0,
    val weeklyCompletionRate: Float = 0f,
    val activeDaysSet: Set<Long> = emptySet(), // Set of Epoch Day numbers
    val totalFocusSessions: Int = 0,
    val totalFocusMinutes: Long = 0L
)

object StreakEngine {

    /**
     * Converts a timestamp in millis to a local-timezone epoch day integer.
     * Uses local midnight to avoid UTC/IST mismatch (tasks done before 05:30 IST
     * would otherwise land on the previous UTC day and break streaks).
     */
    fun toEpochDay(timestampMillis: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestampMillis
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return TimeUnit.MILLISECONDS.toDays(cal.timeInMillis)
    }

    /**
     * Calculates streak stats from all todos and optional focus sessions.
     * Rule: Completing at least one task OR one focus session on a day makes that day an active streak day.
     */
    fun calculateStats(
        allTodos: List<TodoEntity>,
        focusSessions: List<FocusSessionEntity> = emptyList()
    ): StreakStats {
        val completedTodos = allTodos.filter { it.isCompleted && it.completedAt != null }
        val completedSessions = focusSessions.filter { it.isCompleted && !it.isAbandoned && it.completedAt != null }

        val totalFocusMinutes = completedSessions.sumOf { it.actualDurationMillis } / (1000 * 60)
        val totalFocusSessions = completedSessions.size

        val todoDays = completedTodos.map { toEpochDay(it.completedAt!!) }
        val sessionDays = completedSessions.map { toEpochDay(it.completedAt!!) }
        val activeDays = (todoDays + sessionDays).toSortedSet()

        val totalInWeek = getTodosInCurrentWeek(allTodos).size

        if (activeDays.isEmpty()) {
            return StreakStats(
                currentStreak = 0,
                bestStreak = 0,
                totalCompleted = 0,
                weeklyCompleted = 0,
                weeklyTotal = totalInWeek,
                weeklyCompletionRate = 0f,
                activeDaysSet = emptySet(),
                totalFocusSessions = totalFocusSessions,
                totalFocusMinutes = totalFocusMinutes
            )
        }

        val todayEpochDay = toEpochDay(System.currentTimeMillis())

        // Calculate Current Streak
        var currentStreak = 0
        var checkDay = if (activeDays.contains(todayEpochDay)) todayEpochDay else todayEpochDay - 1

        while (activeDays.contains(checkDay)) {
            currentStreak++
            checkDay--
        }

        // Calculate Best Streak historically
        var bestStreak = 0
        var tempStreak = 0
        var prevDay: Long? = null

        for (day in activeDays) {
            if (prevDay == null || day == prevDay + 1) {
                tempStreak++
            } else if (day > prevDay + 1) {
                tempStreak = 1
            }
            if (tempStreak > bestStreak) {
                bestStreak = tempStreak
            }
            prevDay = day
        }

        if (currentStreak > bestStreak) {
            bestStreak = currentStreak
        }

        // Weekly metrics
        val now = Calendar.getInstance()
        now.set(Calendar.HOUR_OF_DAY, 0)
        now.set(Calendar.MINUTE, 0)
        now.set(Calendar.SECOND, 0)
        now.set(Calendar.MILLISECOND, 0)
        now.set(Calendar.DAY_OF_WEEK, now.firstDayOfWeek)
        val startOfWeekMillis = now.timeInMillis

        val weeklyTodos = allTodos.filter {
            val dueThisWeek = it.dueDate != null && it.dueDate >= startOfWeekMillis
            val completedThisWeek = it.completedAt != null && it.completedAt >= startOfWeekMillis
            dueThisWeek || completedThisWeek
        }.distinctBy { it.id }

        val weeklyCompleted = weeklyTodos.count { it.isCompleted }
        val weeklyTotal = weeklyTodos.size.coerceAtLeast(1)
        val weeklyRate = (weeklyCompleted.toFloat() / weeklyTotal.toFloat()).coerceIn(0f, 1f)

        return StreakStats(
            currentStreak = currentStreak,
            bestStreak = bestStreak,
            totalCompleted = completedTodos.size,
            weeklyCompleted = weeklyCompleted,
            weeklyTotal = weeklyTotal,
            weeklyCompletionRate = weeklyRate,
            activeDaysSet = activeDays,
            totalFocusSessions = totalFocusSessions,
            totalFocusMinutes = totalFocusMinutes
        )
    }

    private fun getTodosInCurrentWeek(allTodos: List<TodoEntity>): List<TodoEntity> {
        val now = Calendar.getInstance()
        now.set(Calendar.HOUR_OF_DAY, 0)
        now.set(Calendar.DAY_OF_WEEK, now.firstDayOfWeek)
        val startOfWeek = now.timeInMillis
        return allTodos.filter {
            (it.dueDate != null && it.dueDate >= startOfWeek) ||
                    (it.createdAt >= startOfWeek)
        }
    }
}
