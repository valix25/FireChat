package com.walle.firechat.fragment


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.auth.AuthUI

import com.walle.firechat.R
import com.walle.firechat.SignInActivity
import com.walle.firechat.glide.GlideApp
import com.walle.firechat.util.FirestoreUtil
import com.walle.firechat.util.StorageUtil
import kotlinx.android.synthetic.main.fragment_my_account.*
import kotlinx.android.synthetic.main.fragment_my_account.view.*
import kotlinx.android.synthetic.main.fragment_my_account.view.editText_name
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.newTask
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.toast
import java.io.ByteArrayOutputStream


class MyAccountFragment : Fragment() {

    private val RC_SELECT_IMAGE = 2
    private lateinit var selectedImageBytes: ByteArray
    private var pictureJustChanged = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_account, container, false)
        view.apply {
            imageView_profile_picture.setOnClickListener {
                val intent = Intent().apply {
                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
                }
                startActivityForResult(Intent.createChooser(intent, "Select image"), RC_SELECT_IMAGE)
            }

            btn_save.setOnClickListener {
                if(::selectedImageBytes.isInitialized) {
                    StorageUtil.uploadProfilePhoto(selectedImageBytes) {imagePath ->
                        FirestoreUtil.updateCurrentUser(editText_name.text.toString(), editText_bio.text.toString(),
                            imagePath)
                    }
                } else {
                    FirestoreUtil.updateCurrentUser(editText_name.text.toString(), editText_bio.text.toString(), null)
                }
                toast("Saving!")
            }

            btn_sign_out.setOnClickListener {
                AuthUI.getInstance()
                    .signOut(this@MyAccountFragment.context!!)
                    .addOnCompleteListener {
                        startActivity(intentFor<SignInActivity>().newTask().clearTask())
                    }
            }
        }
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // data.data the actual data inside the data Intent parameter
        if(requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val selectedImagePath = data.data
            val selectedImageBmp = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, selectedImagePath)
            // val selectedImageBmp = ImageDecoder.createSource(this.getContentResolver(), selectedImagePath)
            //TODO: crop center & use round image

            if(selectedImageBmp != null) {
                val outputStream = ByteArrayOutputStream()
                selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                selectedImageBytes = outputStream.toByteArray()

                GlideApp.with(this)
                    .load(selectedImageBytes)
                    .into(imageView_profile_picture)

                pictureJustChanged = true
            }
        }
    }

    override fun onStart() {
        super.onStart()
        FirestoreUtil.getCurrentUser {user ->
            if(this@MyAccountFragment.isVisible) {
                editText_name.setText(user.name)
                editText_bio.setText(user.bio)
                if(!pictureJustChanged && user.profilePicturePath != null)
                    GlideApp.with(this)
                        .load(StorageUtil.pathToReference(user.profilePicturePath))
                        .placeholder(R.drawable.ic_account_circle_black_24dp)
                        .into(imageView_profile_picture)
            }
        }
    }
}
