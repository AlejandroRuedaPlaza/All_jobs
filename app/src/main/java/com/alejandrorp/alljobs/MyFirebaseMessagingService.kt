package com.alejandrorp.alljobs

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService: FirebaseMessagingService() {
    val CHANNEL_ID= "channel"//nombre del canal

    //METODO PARA CREAR EL CANAL
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "getString(R.string.channel_name)"
            val descriptionText = "getString(R.string.channel_description)"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        Log.d("AAAAAAAAAAAAAAAAAAAAA  ",remoteMessage.notification?.body.toString())//linea del log para ver el mensaje

        createNotificationChannel()

        var i: Intent = Intent(this, MainActivity::class.java)
        var pI: PendingIntent = PendingIntent.getActivity(this, 0, i, 0)

         var builder: NotificationCompat.Builder = NotificationCompat.Builder(this, CHANNEL_ID)
             .setSmallIcon(R.mipmap.ic_launcher)
             .setContentTitle(remoteMessage.notification?.title)
             .setContentText(remoteMessage.notification?.body)
             .setPriority(NotificationCompat.PRIORITY_DEFAULT)
             .setAutoCancel(true)
             .setContentIntent(pI)

        var notificationManager:NotificationManager= getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, builder.build())//CREA LA NOTIFICACION

    }

}