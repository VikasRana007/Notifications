package com.learning.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.Builder
import androidx.core.app.RemoteInput

class MainActivity : AppCompatActivity() {
    private lateinit var button: Button

    // define a channel id for the notification
    private val channelID = "com.learning.notification.channel_one"

    // key fot Reply action
    private val KEY_REPLY = "key_reply"

    // Notification manager instance required to create a notification channel instance  define this as var
    // we define it var because we are going to get  using the system service where chances of re assigning the Notification Manager
    private var notificationManager: NotificationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /**
         * create notification manager instance, a notification channel has an id, channel name, a channel description & we create it by
         * creating a notification channel instance and then passing that instance to the createNotificationManager() of the NotificationManager
         * class , lets create a function createNotificationChannel
         */
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        button = findViewById(R.id.button)
        // we need to create the notification before posting any modifications otherwise it can throw NPE(Null Pointer Exception)
        createNotificationChannel(channelID, "Experimental Channel", "This is a Dummy Channel")
        button.setOnClickListener { displayNotification() }
    }

    private fun displayNotification() {
        // first crete a notification channel for notification after channel created
        // here we create the notification using  NotificationCompat

        // pendingIntent objects are the object when we want to use intent at some point in the future
        // in this scenario the object will be used to provide the Notification System
        // with a way to launch the SecondActivity activity when the use taps on the
        // notification.
        // What this FLAG_UPDATE_CURRENT does => when the system create a new intent if the pending intent already exist in the memory
        // system keep it but replace its extra data with what is in this new Intent
        // android allow us to customize these intents by adding flags like this below ->

        // if we use this intent to launch a new activity from an existing activity
        // then the reply target of existing activity will be transferred to the new activity

        val notificationId = 45

        val tapResultInstance = Intent(this, SecondActivity::class.java)
        val pendingIntent: PendingIntent =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getActivity(
                    this,
                    0,
                    tapResultInstance,
                    PendingIntent.FLAG_MUTABLE
                )
            } else {
                PendingIntent.getActivity(
                    this,
                    0,
                    tapResultInstance,
                    PendingIntent.FLAG_IMMUTABLE
                )
            }
//                .apply {Intent.FLAG_ACTIVITY_FORWARD_RESULT
//            }

        // reply action
        // we need to create a remote input instance with the reply key and reply label
        val remoteInput: RemoteInput = RemoteInput.Builder(KEY_REPLY).run {
            setLabel("Insert your name here")
            build()
        }
        // Now action for remote input
        val replyAction = NotificationCompat.Action.Builder(
            0,
            "REPLY",
            pendingIntent,
        ).addRemoteInput(remoteInput).build()

        // To navigate to the details and setting activity using "Action" feature we
        // need to first create an intent, then create a pending intent for that intent and  then create an action with the pending intent
        // action button 1
        val intent2 = Intent(this, DetailsActivity::class.java)
        val pendingIntent2: PendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                intent2,
                PendingIntent.FLAG_IMMUTABLE
            )
//                .apply {Intent.FLAG_ACTIVITY_FORWARD_RESULT
        val action2: NotificationCompat.Action =
            NotificationCompat.Action.Builder(0, "Details", pendingIntent2).build()

        // action button 2
        val intent3 = Intent(this, SettingsActivity::class.java)
        val pendingIntent3: PendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                intent3,
                PendingIntent.FLAG_IMMUTABLE
            )
//                .apply {Intent.FLAG_ACTIVITY_FORWARD_RESULT
        val action3: NotificationCompat.Action =
            NotificationCompat.Action.Builder(0, "Settings", pendingIntent3).build()

        val notification = Builder(this@MainActivity, channelID)
            .setContentTitle("This is Dummy Title")      // Title
            .setContentText("This is Testing Notification")  // Text
            .setSmallIcon(android.R.drawable.ic_dialog_info)  // Icon
            .setAutoCancel(true)    // so the Notification automatically cancelled when user click on the pannel
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)   // for priority high
//            .setContentIntent(pendingIntent)
            .addAction(action2)
            .addAction(action3)
            .addAction(replyAction)
            .build()                // last thing is build
        notificationManager?.notify(notificationId, notification)
    }

    private fun createNotificationChannel(id: String, name: String, channelDescription: String) {
        // SDK version should be oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(id, name, importance).apply {
                description = channelDescription
            }
            // lets register the notification channel with the System using
            // notification manager instance we have created earlier
            notificationManager?.createNotificationChannel(channel)
        }
    }
}
