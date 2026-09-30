package com.anant.sivonotes.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.anant.sivonotes.MainActivity
import com.anant.sivonotes.R
import com.anant.sivonotes.SivoNotesApplication
import com.anant.sivonotes.data.local.entity.ReminderEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra(AlarmScheduler.EXTRA_REMINDER_ID, -1L)
        val title = intent.getStringExtra(AlarmScheduler.EXTRA_REMINDER_TITLE) ?: "Reminder"
        val note = intent.getStringExtra(AlarmScheduler.EXTRA_REMINDER_NOTE) ?: ""

        when (intent.action) {
            AlarmScheduler.ACTION_REMINDER -> {
                showNotification(context, reminderId, title, note)
            }
            AlarmScheduler.ACTION_DONE -> {
                dismissNotification(context, reminderId)
                markReminderComplete(context, reminderId)
            }
            AlarmScheduler.ACTION_SNOOZE -> {
                dismissNotification(context, reminderId)
                snoozeReminder(context, reminderId, title, note)
            }
        }
    }

    private fun showNotification(
        context: Context,
        reminderId: Long,
        title: String,
        note: String
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "sivo_reminders_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Sivo Notes Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Offline local task and note reminders"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Safe requestCode: mask Long to positive Int to avoid overflow collisions
        val safeId = reminderId.and(0x7FFFFFFF).toInt()

        // Tap Intent -> Opens App
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            safeId,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action: Done
        val doneIntent = Intent(context, ReminderReceiver::class.java).apply {
            action = AlarmScheduler.ACTION_DONE
            putExtra(AlarmScheduler.EXTRA_REMINDER_ID, reminderId)
        }
        val donePendingIntent = PendingIntent.getBroadcast(
            context,
            (reminderId + 100000L).and(0x7FFFFFFF).toInt(),
            doneIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action: Snooze 10m
        val snoozeIntent = Intent(context, ReminderReceiver::class.java).apply {
            action = AlarmScheduler.ACTION_SNOOZE
            putExtra(AlarmScheduler.EXTRA_REMINDER_ID, reminderId)
            putExtra(AlarmScheduler.EXTRA_REMINDER_TITLE, title)
            putExtra(AlarmScheduler.EXTRA_REMINDER_NOTE, note)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            (reminderId + 200000L).and(0x7FFFFFFF).toInt(),
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title)
            .setContentText(note.ifBlank { "Task reminder" })
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .addAction(android.R.drawable.ic_menu_today, "Done", donePendingIntent)
            .addAction(android.R.drawable.ic_lock_idle_alarm, "Snooze 10m", snoozePendingIntent)

        notificationManager.notify(safeId, builder.build())
    }

    private fun dismissNotification(context: Context, reminderId: Long) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(reminderId.and(0x7FFFFFFF).toInt())
    }

    private fun markReminderComplete(context: Context, reminderId: Long) {
        if (reminderId <= 0) return
        val app = context.applicationContext as? SivoNotesApplication ?: return
        // goAsync() keeps the BroadcastReceiver alive until the coroutine finishes,
        // preventing Android from killing the process before the DB write completes.
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val reminder = app.container.remindersRepository.getReminderByIdDirect(reminderId)
                if (reminder != null) {
                    if (reminder.repeatRule != "NEVER") {
                        val nextTime = calculateNextOccurrence(reminder.targetTimeMillis, reminder.repeatRule)
                        val nextReminder = reminder.copy(targetTimeMillis = nextTime)
                        app.container.remindersRepository.updateReminder(nextReminder)
                        AlarmScheduler.schedule(context, nextReminder)
                    } else {
                        app.container.remindersRepository.markCompleted(reminder)
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun snoozeReminder(
        context: Context,
        reminderId: Long,
        title: String,
        note: String
    ) {
        val app = context.applicationContext as? SivoNotesApplication ?: return
        val snoozedTime = System.currentTimeMillis() + 10 * 60 * 1000L // 10 minutes later
        // goAsync() keeps the receiver alive until the DB write completes
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Fetch the real reminder from DB to preserve repeatRule and all fields
                val original = app.container.remindersRepository.getReminderByIdDirect(reminderId)
                val snoozedReminder = if (original != null) {
                    original.copy(targetTimeMillis = snoozedTime)
                } else {
                    // Fallback if DB lookup fails (shouldn't happen)
                    ReminderEntity(
                        id = reminderId,
                        title = title,
                        note = note,
                        targetTimeMillis = snoozedTime
                    )
                }
                // Persist the new time so boot-reschedule picks it up
                app.container.remindersRepository.updateReminder(snoozedReminder)
                AlarmScheduler.schedule(context, snoozedReminder)
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        fun calculateNextOccurrence(currentMillis: Long, repeatRule: String): Long {
            val calendar = java.util.Calendar.getInstance().apply {
                timeInMillis = currentMillis
            }
            when (repeatRule.uppercase()) {
                "DAILY" -> calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
                "WEEKDAYS" -> {
                    do {
                        calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
                    } while (calendar.get(java.util.Calendar.DAY_OF_WEEK) == java.util.Calendar.SATURDAY ||
                        calendar.get(java.util.Calendar.DAY_OF_WEEK) == java.util.Calendar.SUNDAY
                    )
                }
                "WEEKLY" -> calendar.add(java.util.Calendar.WEEK_OF_YEAR, 1)
                "MONTHLY" -> calendar.add(java.util.Calendar.MONTH, 1)
                else -> calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
            }
            return calendar.timeInMillis
        }
    }
}
