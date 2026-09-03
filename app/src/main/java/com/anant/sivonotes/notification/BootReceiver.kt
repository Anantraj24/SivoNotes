package com.anant.sivonotes.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.anant.sivonotes.SivoNotesApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            val app = context.applicationContext as? SivoNotesApplication ?: return
            CoroutineScope(Dispatchers.IO).launch {
                val pendingReminders = app.container.remindersRepository.getAllPendingRemindersDirect()
                pendingReminders.forEach { reminder ->
                    if (reminder.targetTimeMillis > System.currentTimeMillis()) {
                        AlarmScheduler.schedule(context, reminder)
                    }
                }
            }
        }
    }
}
