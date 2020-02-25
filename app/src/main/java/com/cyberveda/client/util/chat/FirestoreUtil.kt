package com.cyberveda.client.util.chat

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.cyberveda.client.models.chat.*
import com.cyberveda.client.ui.main.chat.recycler_item.ImageMessageItem
import com.cyberveda.client.ui.main.chat.recycler_item.PersonItem
import com.cyberveda.client.ui.main.chat.recycler_item.TextMessageItem

import com.xwray.groupie.kotlinandroidextensions.Item

// object is basically a singleton. only one instance is present.

object FirestoreUtil {
    private val TAG = "lgx_FirestoreUtil"
    // "lazy" initialization means the value which we get from FirebaseFirestore.getInstance() will be set inside of var firestoreInstance only once we need it. Once we need means when we call Firestore code from below.

    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document(
            "users/${FirebaseAuth.getInstance().currentUser?.uid
                ?: throw NullPointerException("UID is null.")}"
        )

    private val chatChannelsCollectionRef = firestoreInstance.collection("chatChannels")

    // Unit is void in java.

    fun initCurrentUserIfFirstTime(onComplete: () -> Unit) {
        currentUserDocRef.get().addOnSuccessListener { documentSnapshot ->
            if (!documentSnapshot.exists()) {
                val newUser = User(
                    FirebaseAuth.getInstance().currentUser?.displayName ?: "",
                    "", "","","","","","","","","",null, mutableListOf()
                )
                currentUserDocRef.set(newUser).addOnSuccessListener {
                    onComplete()
                }
            } else
                onComplete()
        }
    }

    fun updateCurrentUser(
        name: String = "",
        bio: String = "",
        weight: String = "",
        height: String = "",
        maritalStatus: String = "",
        education: String = "",
        profession: String = "",
        age: String = "",
        gender: String = "",
        eatingHabits: String = "",
        sleepingHabits: String = "",

        profilePicturePath: String? = null
    ) {
        val userFieldMap = mutableMapOf<String, Any>()
        if (name.isNotBlank()) userFieldMap["name"] = name
        if (bio.isNotBlank()) userFieldMap["bio"] = bio
        if (weight.isNotBlank()) userFieldMap["weight"] = weight
        if (height.isNotBlank()) userFieldMap["height"] = height
        if (maritalStatus.isNotBlank()) userFieldMap["maritalStatus"] = maritalStatus
        if (education.isNotBlank()) userFieldMap["education"] = education
        if (profession.isNotBlank()) userFieldMap["profession"] = profession
        if (age.isNotBlank()) userFieldMap["age"] = age
        if (gender.isNotBlank()) userFieldMap["gender"] = gender
        if (eatingHabits.isNotBlank()) userFieldMap["eatingHabits"] = eatingHabits
        if (sleepingHabits.isNotBlank()) userFieldMap["sleepingHabits"] = sleepingHabits



        if (profilePicturePath != null)
            userFieldMap["profilePicturePath"] = profilePicturePath
        currentUserDocRef.update(userFieldMap)
    }

    fun getCurrentUser(onComplete: (User) -> Unit) {
        currentUserDocRef.get()
            .addOnSuccessListener {
                onComplete(it.toObject(User::class.java)!!)
            }
    }

    fun addUsersListener(context: Context, onListen: (List<Item>) -> Unit): ListenerRegistration {
        return firestoreInstance.collection("users")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.e("FIRESTORE", "Users listener error.", firebaseFirestoreException)
                    return@addSnapshotListener
                }

                val items = mutableListOf<Item>()

                if (FirebaseAuth.getInstance().currentUser?.uid == "oR7wbArR5qe49dHXIETAxCQV0QF2") {
                    querySnapshot!!.documents.forEach {

                        // now, even the currently signed in user is inside the collection("users"). we obviously do not want to show current user to himself. So, we filter the current user.

                        if (it.id != FirebaseAuth.getInstance().currentUser?.uid)
                            items.add(PersonItem(it.toObject(User::class.java)!!, it.id, context))
                    }
                } else {
                    querySnapshot!!.documents.forEach {

                        // now, even the currently signed in user is inside the collection("users"). we obviously do not want to show current user to himself. So, we filter the current user.

                        Log.d(TAG, "addUsersListener: 74: ${it.id}")

                        if (it.id == "oR7wbArR5qe49dHXIETAxCQV0QF2") {
                            Log.d(TAG, "addUsersListener: 77: HERE inside takamura")

                            items.add(PersonItem(it.toObject(User::class.java)!!, it.id, context))

                        } else {
                            Log.d(TAG, "addUsersListener: 82 HERE OTHERS: ${it}")
                        }

//                        if (it.id != FirebaseAuth.getInstance().currentUser?.uid){
//                            items.add(PersonItem(it.toObject(User::class.java)!!, it.id, context))
//
//                        }
                    }
                }



                onListen(items)
            }
    }

    fun removeListener(registration: ListenerRegistration) = registration.remove()

    fun getOrCreateChatChannel(
        otherUserId: String,
        onComplete: (channelId: String) -> Unit
    ) {
        currentUserDocRef.collection("engagedChatChannels")
            .document(otherUserId).get().addOnSuccessListener {
                if (it.exists()) {
                    onComplete(it["channelId"] as String)
                    return@addOnSuccessListener
                }

                val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

                val newChannel = chatChannelsCollectionRef.document()
                newChannel.set(ChatChannel(mutableListOf(currentUserId, otherUserId)))

                currentUserDocRef
                    .collection("engagedChatChannels")
                    .document(otherUserId)
                    .set(mapOf("channelId" to newChannel.id))

                firestoreInstance.collection("users").document(otherUserId)
                    .collection("engagedChatChannels")
                    .document(currentUserId)
                    .set(mapOf("channelId" to newChannel.id))

                onComplete(newChannel.id)
            }
    }

    fun addChatMessagesListener(
        channelId: String, context: Context,
        onListen: (List<Item>) -> Unit
    ): ListenerRegistration {
        return chatChannelsCollectionRef.document(channelId).collection("messages")
            .orderBy("time")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.e("FIRESTORE", "ChatMessagesListener error.", firebaseFirestoreException)
                    return@addSnapshotListener
                }

                val items = mutableListOf<Item>()
                querySnapshot!!.documents.forEach {
                    if (it["type"] == MessageType.TEXT)
                        items.add(TextMessageItem(it.toObject(TextMessage::class.java)!!, context))
                    else
                        items.add(
                            ImageMessageItem(
                                it.toObject(ImageMessage::class.java)!!,
                                context
                            )
                        )
                    return@forEach
                }
                onListen(items)
            }
    }

    fun sendMessage(message: Message, channelId: String) {
        chatChannelsCollectionRef.document(channelId)
            .collection("messages")
            .add(message)
    }

    //region FCM
    fun getFCMRegistrationTokens(onComplete: (tokens: MutableList<String>) -> Unit) {
        currentUserDocRef.get().addOnSuccessListener {
            val user = it.toObject(User::class.java)!!
            onComplete(user.registrationTokens)
        }
    }

    fun setFCMRegistrationTokens(registrationTokens: MutableList<String>) {
        currentUserDocRef.update(mapOf("registrationTokens" to registrationTokens))
    }
    //endregion FCM
}