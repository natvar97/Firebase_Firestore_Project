package com.indialone.firebase_firestore_project

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.indialone.firebase_firestore_project.databinding.ActivityRegisterBinding
import com.indialone.firebase_firestore_project.firestore.FireStoreClass
import com.indialone.firebase_firestore_project.models.User

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mBinding: ActivityRegisterBinding
    private var mSelectedImageUri: Uri? = null
    private var mUserProfileImageUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.btnRegister.setOnClickListener(this)
        mBinding.tvLogin.setOnClickListener(this)
        mBinding.ivUserPhoto.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        v?.id?.let {
            when (v.id) {
                R.id.btn_register -> {
                    addDetailsToFireStore()
                }
                R.id.iv_user_photo -> {
                    grantCameraAndStoragePermission()
                }
                R.id.tv_login -> {
                    onBackPressed()
                }
                else -> {
                    // nothing
                }
            }
        }
    }

    private fun submitUserProfileDetails() {
        if (mSelectedImageUri != null) {
            FireStoreClass().uploadImageToCloudStorage(this, mSelectedImageUri)
        } else {
            if (isNotValid()) {
                Toast.makeText(
                    this@RegisterActivity,
                    "Please enter all fields...",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                addDetailsToFireStore()
            }
        }
    }

    private fun addDetailsToFireStore() {
        val email = mBinding.etEmail.text.toString().trim { it <= ' ' }
        val password = mBinding.etPassword.text.toString().trim { it <= ' ' }
        val mobile = mBinding.etMobile.text.toString().trim { it <= ' ' }
        val name = mBinding.etName.text.toString().trim { it <= ' ' }
        val image = mUserProfileImageUrl
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser: FirebaseUser = task.result!!.user!!
                    Log.e("mSelectedImageUri", "$mSelectedImageUri")
                    Log.e("image", image)
                    var user = User(
                        firebaseUser.uid,
                        email,
                        name,
                        mobile
                    )
                    Log.e("error mUserProfile", "$mUserProfileImageUrl is blank")
                    Log.e("error mUserProfile", "$email")
                    Log.e("error mUserProfile", "$name")

                    FireStoreClass().registerUser(this@RegisterActivity, user)

                    val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                        Constants.USER_PROFILE_IMAGE + System.currentTimeMillis() + "." +
                                Constants.getFileExtension(this, mSelectedImageUri)
                    )

                    sRef.putFile(mSelectedImageUri!!).addOnSuccessListener { taskSnapShot ->
                        taskSnapShot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                            mUserProfileImageUrl = uri.toString()
                            val hashmap = HashMap<String, Any>()
                            hashmap["image"] = mUserProfileImageUrl
                            FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
                                .document(FireStoreClass().getCurrentUserId())
                                .update(hashmap)
                                .addOnSuccessListener {
                                    val intent =
                                        Intent(this@RegisterActivity, MainActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
                                }
                        }
                    }


                } else {
                    Toast.makeText(
                        this@RegisterActivity,
                        task.exception!!.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.GALLERY_CODE) {
                data?.let {
                    try {
                        mSelectedImageUri = data.data!!
                        Log.e("tag image uri", "$mSelectedImageUri")
                        Glide.with(this)
                            .load(mSelectedImageUri)
                            .centerCrop()
                            .placeholder(R.drawable.ic_user_placeholder)
                            .into(mBinding.ivUserPhoto)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(
                            this,
                            "image selection failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }


    }

    private fun grantCameraAndStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this@RegisterActivity,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this@RegisterActivity,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Constants.showImageChooser(this@RegisterActivity)
        } else {
            ActivityCompat.requestPermissions(
                this@RegisterActivity,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                ),
                Constants.CAMERA_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.CAMERA_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this@RegisterActivity)
            } else {
                Toast.makeText(
                    this,
                    "Camera and Gallery permission Denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun isNotValid(): Boolean {
        return TextUtils.isEmpty(mBinding.etEmail.text.toString().trim { it <= ' ' })
                || TextUtils.isEmpty(mBinding.etPassword.text.toString().trim { it <= ' ' })
                || TextUtils.isEmpty(mBinding.etName.text.toString().trim { it <= ' ' })
                || TextUtils.isEmpty(mBinding.etMobile.text.toString().trim { it <= ' ' })
    }

    fun registerSuccessfully() {
        Toast.makeText(
            this,
            "You are registered Successfully",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun imageUploadSuccess(uri: String) {
        mUserProfileImageUrl = uri
        Log.e("mUrsePro", mUserProfileImageUrl)
    }

}