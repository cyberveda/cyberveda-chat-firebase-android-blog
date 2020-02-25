package com.cyberveda.client.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.cyberveda.client.util.chat.FirestoreUtil


class MyFirebaseInstanceIDService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        if (FirebaseAuth.getInstance().currentUser != null)
            addTokenToFirestore(token)
    }

//    override fun onTokenRefresh() {
//        val newRegistrationToken = FirebaseInstanceId.getInstance().token
//
//        if (FirebaseAuth.getInstance().currentUser != null)
//            addTokenToFirestore(newRegistrationToken)
//    }

    companion object {
        fun addTokenToFirestore(newRegistrationToken: String?) {
            if (newRegistrationToken == null) throw NullPointerException("FCM token is null.")

            FirestoreUtil.getFCMRegistrationTokens { tokens ->
                if (tokens.contains(newRegistrationToken))
                    return@getFCMRegistrationTokens

                tokens.add(newRegistrationToken)
                FirestoreUtil.setFCMRegistrationTokens(tokens)
            }
        }
    }
}