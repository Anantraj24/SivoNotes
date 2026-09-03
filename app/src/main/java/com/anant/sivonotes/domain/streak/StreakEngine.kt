package com.anant.sivonotes.domain.streak

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
    val activeDaysSet: Set<Long> = emptySet() // Set of Epoch Day numbers
)

object StreakEngine {

    /**
     * Converts a timestamp in millis to an epoch day integer (UTC/Local day number).
     */
    fun toEpochDay(timestampMillis: Long): Long {
        return TimeUnit.MILLISECONDS.toDays(timestampMillis)
    }

    /**
     * Calculates streak stats from all todos and completed history.
     * Rule: Completing at least one task on a day makes that day an active streak day.
     */
    fun calculateStats(allTodos: List<TodoEntity>): StreakStats {
        val completedTodos = allTodos.filter { it.isCompleted && it.completedAt != null }
        if (completedTodos.isEmpty()) {
            val totalInWeek = getTodosInCurrentWeek(allTodos).size
            return StreakStats(
                currentStreak = 0,
                bestStreak = 0,
                totalCompleted = 0,
                weeklyCompleted = 0,
                weeklyTotal = totalInWeek,
                weeklyCompletionRate = 0f,
                activeDaysSet = emptySet()
            )
        }

        val activeDays = completedTodos
            .map { toEpochDay(it.completedAt!!) }
            .toSortedSet()

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
            (it.dueDate != null && it.dueDate >= startOfWeekMillis) ||
                    (it.completedAt != null && it.completedAt >= startOfWeekMillis)
        }
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
            activeDaysSet = activeDays
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
