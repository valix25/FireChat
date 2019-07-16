package com.walle.firechat.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.walle.firechat.model.User

// object is basically a singleton so only one is present at any single moment
// Object declaration's initialization is thread-safe.
// To refer to the object, we use its name directly
object FirestoreUtil {

    // by lazy we imply that the value we get from FirebaseFirestore.getInstance() will be set inside firestoreInstance
    // property only when we need it
    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    // Firestore is comprised of documents and collections, all documents must be stored in collections.
    // Documents live in collections, which are simply containers for documents.
    // Documents can contain subcollections and nested objects, both of which can include primitive fields like strings
    // or complex objects like lists
    // Collections and documents are created implicitly in Cloud Firestore. Simply assign data to a document within
    // a collection. If either the collection or document does not exist, Cloud Firestore creates it.
    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document("users/${FirebaseAuth.getInstance().uid 
            ?: throw NullPointerException("UID is null")}")

    fun initCurrentUserIfFirstTime(onComplete: () -> Unit) {
        currentUserDocRef.get().addOnSuccessListener {
            if(!it.exists()) {
                val newUser = User(FirebaseAuth.getInstance().currentUser?.displayName ?: "",
                    "", null)
                currentUserDocRef.set(newUser).addOnSuccessListener {
                    onComplete()
                }
            } else {
                onComplete()
            }
        }
    }

    fun updateCurrentUser(name: String = "", bio: String = "", profilePicturePath: String? = null) {
        val userFieldMap = mutableMapOf<String, Any>()
        if(name.isNotBlank()) userFieldMap["name"] = name
        if(bio.isNotBlank()) userFieldMap["bio"] = bio
        if(profilePicturePath != null) userFieldMap["profilePicturePath"] = profilePicturePath
        currentUserDocRef.update(userFieldMap)
    }

    fun getCurrentUser(onComplete: (User) -> Unit) {
        currentUserDocRef.get().addOnSuccessListener {
            it.toObject(User::class.java)?.let { it1 -> onComplete(it1) }
        }
    }
}