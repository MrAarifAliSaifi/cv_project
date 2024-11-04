package com.example.cvproject.activites.activity.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import cvproject.blinkit.R
import java.util.Random

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val channelID = "class-update"
    private val channelName = "class-updates"

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_foreground)

        // Set up the notification builder
        val builder = NotificationCompat.Builder(this, channelID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setLargeIcon(largeIcon)
            .setColor(ContextCompat.getColor(this, R.color.black))
            .setContentTitle(message.data["title"])
            .setContentText(message.data["body"])
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            .setAutoCancel(true)
            .setOngoing(false)
            .setLights(ContextCompat.getColor(this, R.color.black), 5000, 5000)

        // Check notification permission for Android 13 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Handle lack of permission, log or request permission here if needed
                return
            }
        }

        // Show the notification
        NotificationManagerCompat.from(this).notify(Random().nextInt(3000), builder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            channelID,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
}