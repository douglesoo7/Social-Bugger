package com.dougles.project.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.dougles.project.myapplication.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class AccountSettingsActivity : AppCompatActivity() {

    private lateinit var firebaseUser: FirebaseUser
    private var checker = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        userInfo()

        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        ivSaveAcSettings.setOnClickListener {
            if (checker == "clicked") {

            } else {
                updateUserInfoOnly()
            }
        }
    }

    private fun updateUserInfoOnly() {

        when {
            etFullNameAccountSettings.text.toString() == "" -> {
                Toast.makeText(this, "Full Name is required", Toast.LENGTH_SHORT).show()
            }
            etUsernameAccountSettings.text.toString() == "" -> {
                Toast.makeText(this, "Please write your username", Toast.LENGTH_SHORT).show()
            }
            etBioAccountSettings.text.toString() == "" -> {
                Toast.makeText(this, "Please write your bio", Toast.LENGTH_SHORT).show()
            }
            else -> {

                val userRef =
                    FirebaseDatabase.getInstance().reference.child("Users")

                val usersMap = HashMap<String, Any>()
                usersMap["fullName"] = etFullNameAccountSettings.text.toString()
                usersMap["userName"] = etUsernameAccountSettings.text.toString()
                usersMap["bio"] = etBioAccountSettings.text.toString()

                userRef.child(firebaseUser.uid).updateChildren(usersMap)


                Toast.makeText(this, "Account Info Updated Successfully", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun userInfo() {
        val userRef =
            FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser.uid)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(User::class.java)
                    Picasso.get().load(user?.getImage()).placeholder(R.drawable.profile)
                        .into(ivProfilePicAccountSettings)

                    etUsernameAccountSettings.setText(user?.getUserName())
                    etFullNameAccountSettings.setText(user?.getFullName())
                    etBioAccountSettings.setText(user?.getBio())

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}