package com.dougles.project.myapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dougles.project.myapplication.R
import com.dougles.project.myapplication.model.Post
import com.dougles.project.myapplication.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.post_item_layout.view.*

class PostAdapter(
    private val mContext: Context,
    private val mPosts: List<Post>
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private var firebaseUser: FirebaseUser? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.post_item_layout, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        val post = mPosts[position]
        holder.setData(post)
    }

    override fun getItemCount(): Int {
        return mPosts.size
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(post: Post) {
            val usersRef =
                FirebaseDatabase.getInstance().reference.child("Users").child(post.publisher!!)

            usersRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user = snapshot.getValue<User>(User::class.java)
                        itemView.apply {
                            Picasso.get().load(post.postImage).into(post_image_home)
                            Picasso.get().load(user?.getImage()).placeholder(R.drawable.profile)
                                .into(user_profile_home)
                            publisher.text = user?.getFullName()
                            user_name_home.text = user?.getUserName()
                            description.text = post.description

                        }


                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }


    }

}

fun publisherInfo() {

}



