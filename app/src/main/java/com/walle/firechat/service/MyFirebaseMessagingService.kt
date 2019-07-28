package com.walle.firechat.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


// This is where we will receive messages from the Firebase Cloud Messaging
class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)
        print("On message received called")
        if(remoteMessage?.notification != null) {
            // show notification if we are not in the chat channel from which the incoming message was sent
            Log.d("FCM", remoteMessage.data.toString())
        }
    }
}