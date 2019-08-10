package com.walle.firechat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.startActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser == null || !currentUser.isEmailVerified) {
            startActivity<SignInActivity>()
        } else {
            startActivity<MainActivity>()
        }
        finish()
    }
}
