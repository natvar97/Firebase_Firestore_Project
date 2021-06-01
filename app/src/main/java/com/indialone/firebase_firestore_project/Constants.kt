package com.indialone.firebase_firestore_project

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {
    const val USER_ID = "user_id"
    const val USER_EMAIL = "user_email"
    const val USERS_COLLECTION = "users"
    const val USERS_PREFERENCE = "users_preferences"
    const val LOGGED_ID_USERNAME = "logged_in_username"
    const val CAMERA_CODE = 1
    const val GALLERY_CODE = 1
    const val USER_PROFILE_IMAGE: String = "User_Profile_Image"

    fun showImageChooser(activity: Activity) {
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        activity.startActivityForResult(intent , GALLERY_CODE)
    }

    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }

}