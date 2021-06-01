package com.indialone.firebase_firestore_project

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.indialone.firebase_firestore_project.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mBinding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(mBinding.root)


        mBinding.btnSubmit.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        v?.id?.let {
            when (v.id) {
                R.id.btn_submit -> {
                    val email = mBinding.etEmail.text.toString().trim { it <= ' ' }

                    if (TextUtils.isEmpty(email)) {
                        Toast.makeText(this, "Please enter valid email address", Toast.LENGTH_SHORT)
                            .show()
                    } else {

                        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                            .addOnCompleteListener { task ->

                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        this,
                                        "Email sent successfully...",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    finish()

                                } else {
                                    Toast.makeText(
                                        this,
                                        task.exception!!.message.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }

                }
                else -> {
                    // nothing
                }
            }
        }
    }
}