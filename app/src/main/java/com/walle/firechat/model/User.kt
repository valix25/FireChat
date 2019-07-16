package com.walle.firechat.model

data class User(val name: String,
                val bio: String,
                val profilePicturePath: String?) {
    // Firestore need a parameter-less constructor
    constructor():this("", "", null)
}