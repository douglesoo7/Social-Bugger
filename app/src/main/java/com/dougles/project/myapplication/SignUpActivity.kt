package com.dougles.project.myapplication

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        btnSignInSignUp.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        btnSignUp.setOnClickListener {
            createAccount()
        }
    }

    private fun createAccount() {
        etUsernameAccountSettings
        val fullName = etFullNameSignUp.text.toString()
        val email = etEmailSignUp.text.toString()
        val userName = etUsernameSignUp.text.toString()
        val password = etPasswordSignUp.text.toString()

        when {
            TextUtils.isEmpty(fullName) -> Toast.makeText(
                this,
                "Full Name can't be empty",
                Toast.LENGTH_SHORT
            ).show()
            TextUtils.isEmpty(userName) -> Toast.makeText(
                this,
                "Full Name can't be empty",
                Toast.LENGTH_SHORT
            ).show()
            TextUtils.isEmpty(email) -> Toast.makeText(
                this,
                "Full Name can't be empty",
                Toast.LENGTH_SHORT
            ).show()
            TextUtils.isEmpty(password) -> Toast.makeText(
                this,
                "Full Name can't be empty",
                Toast.LENGTH_SHORT
            ).show()

            else -> {

                val progressDialog = ProgressDialog(this)
                progressDialog.apply {
                    setTitle("Sign Up")
                    setMessage("Please wait, this may take a while...")
                    setCanceledOnTouchOutside(false)
                    show()
                }


                val mAuth = FirebaseAuth.getInstance()
                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            saveUserInfo(fullName, userName, email, progressDialog)
                        } else {
                            val message = task.exception.toString()
                            Toast.makeText(this, "Error:$message", Toast.LENGTH_SHORT).show()
                            mAuth.signOut()
                            progressDialog.dismiss()
                        }
                    }
            }
        }
    }

    private fun saveUserInfo(
        fullName: String,
        userName: String,
        email: String,
        progressDialog: ProgressDialog
    ) {
        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        val userRef = FirebaseDatabase.getInstance().reference.child("Users")
        val usersMap = HashMap<String, Any>()
        usersMap["uid"] = currentUserId
        usersMap["fullName"] = fullName
        usersMap["userName"] = userName
        usersMap["email"] = email
        usersMap["bio"] = "Update your bio to connect with more people."
        usersMap["image"] =
            "https://firebasestorage.googleapis.com/v0/b/social-bugger.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=dfd96e88-b0f9-4a04-9cd8-9e422185e192"
        userRef.child(currentUserId).setValue(usersMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Account created Successfully", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            } else {
                val message = task.exception.toString()
                Toast.makeText(this, "Error:$message", Toast.LENGTH_SHORT).show()
                FirebaseAuth.getInstance().signOut()
                progressDialog.dismiss()
            }

        }


    }
}