package com.indialone.firebase_firestore_project.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.indialone.firebase_firestore_project.Constants
import com.indialone.firebase_firestore_project.LoginActivity
import com.indialone.firebase_firestore_project.RegisterActivity
import com.indialone.firebase_firestore_project.models.User

class FireStoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()


    fun registerUser(activity: RegisterActivity, user: User) {

        mFireStore.collection(Constants.USERS_COLLECTION)
            .document(user.id)
            .set(user, SetOptions.merge())
            .addOnSuccessListener {
                activity.registerSuccessfully()
            }
            .addOnFailureListener { e ->
                Log.e("error in registering", e.message.toString())
            }

    }

    fun uploadImageToCloudStorage(activity: Activity, imageUri: Uri?) {
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            Constants.USER_PROFILE_IMAGE + System.currentTimeMillis() + "." +
                    Constants.getFileExtension(activity, imageUri)
        )

        sRef.putFile(imageUri!!).addOnSuccessListener { taskSnapShot ->
            taskSnapShot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->

                Log.e("tag downloadable url" , "$uri")

                when (activity) {
                    is RegisterActivity -> {
                        activity.imageUploadSuccess(uri.toString())
                    }
                }
            }
        }

    }

    fun getCurrentUserId(): String {

        val currentUser = FirebaseAuth.getInstance().currentUser

        var currentUserId = ""
        if (currentUser != null) {
            currentUserId = currentUser.uid
        }

        return currentUserId
    }

    fun getUserDetails(activity: Activity) {

        mFireStore.collection(Constants.USERS_COLLECTION)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->

                val user = document.toObject(User::class.java)

                val sharedPreferences = activity
                    .getSharedPreferences(
                        Constants.USERS_PREFERENCE,
                        Context.MODE_PRIVATE
                    )

                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(
                    Constants.LOGGED_ID_USERNAME,
                    "UserId : ${user!!.id} \nUsername : ${user.name} \nEmail : ${user.email} \nMobile No : ${user.mobile}"
                )
                editor.apply()


                when (activity) {
                    is LoginActivity -> {
                        activity.loggedInSuccess(user)
                    }
                }

            }

    }

}