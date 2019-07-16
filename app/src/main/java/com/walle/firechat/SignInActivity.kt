package com.walle.firechat

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.walle.firechat.util.FirestoreUtil
import kotlinx.android.synthetic.main.activity_sign_in.*
import org.jetbrains.anko.*
import org.jetbrains.anko.design.longSnackbar

class SignInActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 1

    private val signInProviders = listOf(
        AuthUI.IdpConfig.EmailBuilder()
            .setAllowNewAccounts(true)
            .setRequireName(true)
            .build()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        account_sign_in.setOnClickListener {
            val intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(signInProviders)
                .setLogo(R.drawable.ic_filter_vintage_color_primary_126dp)
                .setTheme(R.style.AppTheme)
                .build()
            startActivityForResult(intent, RC_SIGN_IN)
        }
        //TODO: add email verification (maybe password reset as well?)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if(resultCode == Activity.RESULT_OK) {
                val progressDialog = indeterminateProgressDialog("Setting up your account")
                FirestoreUtil.initCurrentUserIfFirstTime {
                    startActivity(intentFor<MainActivity>().newTask().clearTask())
                    progressDialog.dismiss()
                }
            } else if(resultCode == Activity.RESULT_CANCELED) {
                if (response == null) return

                when(response.error?.errorCode) {
                    ErrorCodes.NO_NETWORK -> this.contentView?.longSnackbar("No network")
                    ErrorCodes.UNKNOWN_ERROR -> this.contentView?.longSnackbar("Unknown error")
                }
            }
        }
    }
}
