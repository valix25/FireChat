package com.walle.firechat.recyclerview.item

import android.content.Context
import com.walle.firechat.R
import com.walle.firechat.glide.GlideApp
import com.walle.firechat.model.User
import com.walle.firechat.util.StorageUtil
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_person.*

class PersonItem(val user: User,
                 val userId: String,
                 private val context: Context)
    : Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView_name.text = user.name
        viewHolder.textView_bio.text = user.bio
        if(user.profilePicturePath != null) {
            GlideApp.with(context)
                .load(StorageUtil.pathToReference(user.profilePicturePath))
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .into(viewHolder.imageView_profile_picture)
        }
    }

    override fun getLayout() = R.layout.item_person

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is PersonItem) {
            return false
        }
        return this.user == other.user && this.userId == other.userId
    }

    override fun hashCode(): Int {
        var result = user.hashCode()
        result = 31 * result + userId.hashCode()
        return result
    }
}