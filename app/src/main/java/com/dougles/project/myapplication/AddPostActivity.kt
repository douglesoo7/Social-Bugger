package com.dougles.project.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.app.ProgressDialog
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.tasks.OnFailureListener
import android.widget.Toast
import android.webkit.MimeTypeMap
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.activity_add_post.*
import java.util.HashMap

class AddPostActivity : AppCompatActivity() {
    private var myUrl = ""

    private var imageUri: Uri? = null
    private lateinit var storageProfilePicReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        storageProfilePicReference =
            FirebaseStorage.getInstance().reference.child("Posts Pictures")


        close.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@AddPostActivity, MainActivity::class.java))
            finish()
        })

        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickIntent.type = "image/*"
        val chooserIntent = Intent.createChooser(intent, "Select Image")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))
        startActivityForResult(chooserIntent, 10)
        post.setOnClickListener {
            uploadImage()
        }

    }

    private fun uploadImage() {
        when {
            imageUri == null -> {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            }

            else -> {

                val progressDialog = ProgressDialog(this)
                progressDialog.apply {
                    setTitle("Uploading")
                    setMessage("Please wait...")
                    show()
                }

                val fileRef =
                    storageProfilePicReference.child(System.currentTimeMillis().toString() + ".jpg")
                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)

                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                            progressDialog.dismiss()
                        }
                    }

                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener(OnCompleteListener<Uri> { task ->
                    if (task.isSuccessful) {
                        progressDialog.dismiss()
                        val downloadUri = task.result
                        myUrl = downloadUri.toString()

                        val reference = FirebaseDatabase.getInstance().reference.child("Posts")
                        val postId = reference.push().key

                        val postMap = HashMap<String, Any>()
                        postMap["postId"] = postId!!
                        postMap["description"] = etDescriptionPost.text.toString()
                        postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                        postMap["postImage"] = myUrl

                        reference.child(postId).updateChildren(postMap)

                        Toast.makeText(
                            this,
                            "Uploaded Successfully",
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10 && resultCode == RESULT_OK) {
            imageUri = data!!.data
            ivImagePost!!.setImageURI(imageUri)
        } else {
            Toast.makeText(this, "Error, Try again!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}