package com.dougles.project.myapplication

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import com.dougles.project.myapplication.model.User
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class AccountSettingsActivity : AppCompatActivity() {

    private lateinit var firebaseUser: FirebaseUser
    private var checker = ""
    private var myUrl = ""

    private var imageUri: Uri? = null
    private lateinit var storageProfilePicReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        storageProfilePicReference =
            FirebaseStorage.getInstance().reference.child("Profile Pictures")

        getUserInfoFromFirebase()


        ivProfilePicAccountSettings.setOnClickListener {
            checker = "clicked"
            val pickIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "image/*"

            val chooserIntent = Intent.createChooser(intent, "Select Image")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

            startActivityForResult(chooserIntent, 10)
        }
        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        ivSaveAcSettings.setOnClickListener {
            if (checker == "clicked") {

                uploadImageAndUpdateInfo()

            } else {
                updateUserInfoOnly()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 10 && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            ivProfilePicAccountSettings.setImageURI(imageUri)
        } else {
            Toast.makeText(this, "Error Try Again", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getUserInfoFromFirebase() {
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

    private fun uploadImageAndUpdateInfo() {

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
            imageUri == null -> {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            }

            else -> {

                val progressDialog = ProgressDialog(this)
                progressDialog.apply {
                    setTitle("Account Settings")
                    setMessage("Please wait, we are updating you profile...")
                    show()
                }

                val fileRef = storageProfilePicReference.child(firebaseUser.uid + ".jpg")
                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)
                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }

                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener(OnCompleteListener<Uri> { task ->
                    if (task.isSuccessful) {
                        progressDialog.dismiss()
                        val downloadUri = task.result
                        myUrl = downloadUri.toString()

                        val reference = FirebaseDatabase.getInstance().reference.child("Users")

                        val usersMap = HashMap<String, Any>()
                        usersMap["fullName"] = etFullNameAccountSettings.text.toString()
                        usersMap["userName"] = etUsernameAccountSettings.text.toString()
                        usersMap["bio"] = etBioAccountSettings.text.toString()
                        usersMap["image"] = myUrl

                        reference.child(firebaseUser.uid).updateChildren(usersMap)

                        Toast.makeText(
                            this,
                            "Account Info Updated Successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        progressDialog.dismiss()
                    } else {
                        progressDialog.dismiss()
                    }
                })
            }
        }
    }

}