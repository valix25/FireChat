package com.walle.firechat.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


// This is where we will receive messages from the Firebase Cloud Messaging
class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        //super.onMessageReceived(p0)
        if(remoteMessage.notification != null) {
            // TODO: show notification
            Log.d("FCM", "FCM message received")
        }
    }
}