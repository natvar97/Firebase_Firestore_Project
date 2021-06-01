package com.indialone.firebase_firestore_project

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.indialone.firebase_firestore_project.databinding.ActivityLoginBinding
import com.indialone.firebase_firestore_project.firestore.FireStoreClass
import com.indialone.firebase_firestore_project.models.User

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mBinding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.btnLogin.setOnClickListener(this)
        mBinding.tvRegister.setOnClickListener(this)
        mBinding.tvForgotPassword.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        v?.id?.let {
            when (v.id) {
                R.id.btn_login -> {
                    if (isNotValid()) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Please provide valid values...",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val email = mBinding.etEmail.text.toString().trim { it <= ' ' }
                        val password = mBinding.etPassword.text.toString().trim { it <= ' ' }
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {

                                    FireStoreClass().getUserDetails(this@LoginActivity)

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
                R.id.tv_register -> {
                    startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
                }
                R.id.tv_forgot_password -> {
                    startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
                }
                else -> {
                    // nothing
                }
            }
        }
    }

    private fun isNotValid(): Boolean {
        return TextUtils.isEmpty(mBinding.etEmail.text.toString().trim { it <= ' ' })
                || TextUtils.isEmpty(mBinding.etPassword.text.toString().trim { it <= ' ' })
    }

    fun loggedInSuccess(user: User?) {

        Toast.makeText(
            this,
            "You are login successfully",
            Toast.LENGTH_SHORT
        ).show()

        Log.e("tag email", user!!.email)
        Log.e("tag name", user.name)
        Log.e("tag mobile", user.mobile)

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}