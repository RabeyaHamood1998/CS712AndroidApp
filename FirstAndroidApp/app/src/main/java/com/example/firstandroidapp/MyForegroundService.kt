package com.example.firstandroidapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat

class MyForegroundService : Service() {

    private val channelId = "service_channel"
    private val channelName = "Service Channel"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        // ✅ Proof that the SERVICE actually started (not just the button)
        Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show()

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Service")
            .setContentText("The service has started")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        // ✅ Start foreground (this should make the notification appear)
        try {
            startForeground(1, notification)
        } catch (e: Exception) {
            // If Android blocks foreground start for any reason, fail loudly
            Toast.makeText(this, "Foreground service blocked: ${e.message}", Toast.LENGTH_LONG).show()
            stopSelf()
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(this, "Service stopped", Toast.LENGTH_SHORT).show()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)

            // Don't recreate if it already exists
            val existing = manager.getNotificationChannel(channelId)
            if (existing == null) {
                val channel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_LOW
                )
                manager.createNotificationChannel(channel)
            }
        }
    }
}
