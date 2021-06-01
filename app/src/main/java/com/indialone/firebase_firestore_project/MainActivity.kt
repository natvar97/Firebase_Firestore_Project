package com.indialone.firebase_firestore_project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.indialone.firebase_firestore_project.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val sharedPreferences = getSharedPreferences(Constants.USERS_PREFERENCE, Context.MODE_PRIVATE)
        val email = sharedPreferences.getString(Constants.LOGGED_ID_USERNAME, null)


        mBinding.tvEmail.text = email

        mBinding.btnLogout.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        v?.id?.let {
            when (v.id) {
                R.id.btn_logout -> {

                    FirebaseAuth.getInstance().signOut()

                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finish()
                }
            }
        }
    }
}