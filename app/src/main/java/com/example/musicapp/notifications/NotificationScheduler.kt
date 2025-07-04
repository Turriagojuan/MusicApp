package com.example.musicapp.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.concurrent.TimeUnit

object NotificationScheduler {

    private const val NOTIFICATION_ID = 1
    // --- INICIO DEL CAMBIO ---
    // Se cambia el intervalo de 3 días a 1 minuto para depuración.
    private val REMINDER_INTERVAL_MILLIS = TimeUnit.MINUTES.toMillis(1) // 1 minuto en milisegundos
    // --- FIN DEL CAMBIO ---

    fun scheduleNotification(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Se calcula el tiempo de activación: ahora + 1 minuto.
        val triggerAtMillis = System.currentTimeMillis() + REMINDER_INTERVAL_MILLIS

        // Se programa la alarma.
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )
    }

    fun cancelNotification(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}