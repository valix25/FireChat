package com.walle.firechat.service

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.walle.firechat.util.FirestoreUtil


// Firebase Cloud Messaging uses tokens to identify devices. We need to remember the token of the device as well as
// which user is using the current device by saving it in Firebase Firestore database. Then we can send messages using
// Firebase Cloud Messaging to all devices that belong to a certain user.
class MyFirebaseInstanceIDService: FirebaseMessagingService() {

    override fun onNewToken(token: String?) {
        super.onNewToken(token)
        // Log.d("NEW_TOKEN", token)

        if (FirebaseAuth.getInstance().currentUser != null)
            addTokenToFirestore(token)
    }

    companion object {
        fun addTokenToFirestore(newRegistrationToken: String?) {
            if(newRegistrationToken == null) throw NullPointerException("FCM token is null")

            FirestoreUtil.getFCMRegistrationTokens {tokens ->  
                if(tokens.contains(newRegistrationToken))
                    return@getFCMRegistrationTokens

                // update firestore with 'new' tokens
                tokens.add(newRegistrationToken)
                FirestoreUtil.setFCMRegistrationTokens(tokens)
            }
        }
    }
}